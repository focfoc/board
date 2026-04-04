import http from 'k6/http';
import { check } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:8080';
const ARTICLE_NO = __ENV.ARTICLE_NO || '1';
const PATH = __ENV.PATH_TEMPLATE || '/articles/{articleNo}';

const requestFailRate = new Rate('view_api_fail_rate');
const viewApiDuration = new Trend('view_api_duration');
const successCount = new Counter('view_api_success_count');

function buildUrl() {
  return `${BASE_URL}${PATH.replace('{articleNo}', ARTICLE_NO)}`;
}

export const options = {
  scenarios: {
    hot_key: {
      executor: 'ramping-arrival-rate',
      startRate: 0,
      timeUnit: '1s',
      preAllocatedVUs: 30,
      maxVUs: 150,
      stages: [
          { target: 100,  duration: '30s' },
          { target: 300, duration: '30s' },
          { target: 700, duration: '30s' },
          { target: 700, duration: '210s' },
      ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
    checks: ['rate>0.99'],
    view_api_fail_rate: ['rate<0.01'],
    view_api_duration: ['p(95)<500'],
  },
};

export default function () {
  const url = buildUrl();

  const res = http.get(url, {
    headers: {
      Accept: 'application/json',
    },
    tags: {
      api: 'get_article',
      articleNo: ARTICLE_NO,
    },
  });

  const ok = check(res, {
    'status is 200': (r) => r.status === 200,
  });

  requestFailRate.add(!ok);
  viewApiDuration.add(res.timings.duration);

  if (ok) {
    successCount.add(1);
  }

  // 실패 원인 확인이 필요하면 잠깐 열어두면 됨
  // if (!ok) {
  //   console.log(`status=${res.status}, body=${res.body}`);
  // }
}

export function handleSummary(data) {
  return {
    stdout: [
      '',
      '===== k6 summary =====',
      `iterations: ${data.metrics.iterations?.values?.count ?? 0}`,
      `http requests: ${data.metrics.http_reqs?.values?.count ?? 0}`,
      `success count: ${data.metrics.view_api_success_count?.values?.count ?? 0}`,
      `http failed rate: ${data.metrics.http_req_failed?.values?.rate ?? 0}`,
      `p95 duration(ms): ${data.metrics.http_req_duration?.values?.['p(95)'] ?? 0}`,
      `avg duration(ms): ${data.metrics.http_req_duration?.values?.avg ?? 0}`,
      '======================',
      '',
    ].join('\n'),
  };
}