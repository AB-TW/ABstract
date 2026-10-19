# Security

## Reporting a vulnerability

Report vulnerabilities privately through GitHub Security Advisories:

https://github.com/AB-TW/ABstract/security/advisories/new

If that page is unavailable, open a GitHub issue that only asks for a private contact. Leave out exploit details, credentials, and customer data.

请通过上面的 GitHub Security Advisories 私下报告漏洞。不要在公开 Issue 里贴出利用细节、真实口令或用户数据。

## Demo configuration

Compose files, `couchbase.Dockerfile`, and the sample `application*.yml` files use local demo passwords only (`password`, `bucket1pass`, `abstract`). Replace them before any shared deployment.

Do not commit `.env` files, `*.mv.db` databases, `upload-files/`, or real host addresses. Frontend defaults belong in `frontend/.env.example`.
