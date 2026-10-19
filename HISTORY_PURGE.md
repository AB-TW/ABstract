# 历史清理记录（git-filter-repo）

本次在私有仓库上重写了历史，使下列路径不再出现在 `main` 和标签 `v0.0.1` 的任何提交中。仓库可见性未改，仍然是 private。

## 从所有已推送的分支和标签中删除的路径

- `upload-files/`
- `txt_docs/`
- `frontend/.env.production`
- `frontend/.env.development`
- `frontend/.env.staging`
- `frontend/frontend.iml`
- `snapadmin_internal.mv.db`
- `main-r2dbc/tmp/test-database.mv.db`
- 任意路径下的 `*.mv.db`

`frontend/.env.production` 里的 WebSocket 主机是一个公网 IP。除按路径删除该文件外，又用 `git filter-repo --replace-text` 对该 IP 字面量做了整库替换。重写后的分支和标签里，`git grep` 已找不到该 IP。

保留了 `frontend/.env.example`。当前 `main` 上的 `resource/abstract-intro.mp4` 和 `remotion-video/` 素材没有放在被删路径下，因此仍在。

## 实际更新的远程引用

GitHub 上当时只广告了这两个引用，二者都已强制更新：

| 引用 | 旧 SHA | 新 SHA |
| --- | --- | --- |
| `refs/heads/main` | `4db31b02bf9111334795f1ad1fee784aef0aeb85` | `19a454a851243910ba9386153c3120d27743e1ac` |
| `refs/tags/v0.0.1` | `408aed33c94c0781823e19139f8fdadec069da34` | `5918e47928ae6d7aa63765f4b6130180482e8deb` |

标签指向的提交由 `435bb51eb82203d7417e71b9b2f59d871b0b536e` 变为 `327720af53c62ae03acd52317dde4722ce000a4e`。

本地审计里还能看到、但 GitHub 分支列表和 `git ls-remote` 已经不存在的分支（`dev`、`develop/*`、`feature/*`、`release/0.0.1`、各 `cursor/*`）没有被重新创建。它们的旧提交要么已经在 `main` 的历史上（重写后换成了新 SHA），要么只剩 Pull Request 的隐藏引用还能指到旧对象。

## 体积

| 范围 | pack 大约 |
| --- | --- |
| 重写前的镜像（仅当时的远程对象） | 33.99 MiB |
| 重写后的 `main` + 标签 | 32.15 MiB |
| 普通克隆并 gc 之后 | 32.11 MiB |

被删路径对应的不重复 blob 约 77 个、未压缩约 2.0 MiB。pack 只小了约 1.8 MiB，因为体积主要在刻意留下的媒体，以及历史里的 `lib/groovy-*.jar`。

## 仍须人工处理

1. **Actions 仓库口令。** 工作流使用 Actions secret `REGISTRY_PASSWORD`（以及成套的 `REGISTRY_HOST`、`REGISTRY_USERNAME`、`REGISTRY_IMAGE`）。这次没有在 git 历史里发现写死的 registry 口令。如果 Variables 里还留着仓库口令，请在 GitHub 上轮换 secret，并删掉对应 variable。git 重写不会改 Actions 里保存的值。
2. **Pull Request 隐藏引用。** `refs/pull/*/head` 共 58 个。除 `refs/pull/83/head`（PR #83，工作区已干净）外，其余 57 个的树里仍有 `frontend/.env.production`。GitHub 对 `DELETE /git/refs/pull/...` 返回 `422 refs/pull/* is read-only`，无法强制更新。这些引用会让旧对象在服务端继续可达，普通 `git clone` 默认不会取出它们，但 `git fetch origin pull/<n>/head` 或已知旧 SHA 仍然可以读到。
3. **在 Support 清除缓存之前不要公开仓库。** 打开 <https://support.github.com/contact>，选择私有仓库 `AB-TW/ABstract`，说明已经用 filter-repo 重写并强制推送了 `main` 和 `v0.0.1`，请他们清除 Pull Request 引用与缓存页面中的敏感数据，并回收重写后不可达的对象。
4. 已有的本地克隆、fork 和工作副本里仍有旧历史。协作者需要重新克隆，不要再把旧历史推回来。
5. 三个已删除的前端 env 文件含有 `VITE_BASE_API`、`VITE_FILE_SERVICE_URL`、`VITE_WEB_SOCKET_URL`。这些地址应视为已经暴露。

## 可选的后续体积清理

当前 `main` 仍包含：

- `resource/abstract-intro.mp4`（约 6.5 MiB，历史中另有一份约 3.1 MiB）
- `remotion-video/public/audio/mixkit-tech-house-vibes-130.mp3`（约 3.3 MiB）
- Remotion 截图（例如 `main-dashboard.png` 约 1.3 MiB）

历史中、但当前 `main` 树上已经没有的 `lib/groovy-4.0.2.jar`（约 7.4 MiB）及 sources jar 也可以另做一次历史删除。这些都不在本次删除名单里。若还要为开源再减包体，媒体文件更适合 Git LFS，而不是和密钥混在同一次重写里。
