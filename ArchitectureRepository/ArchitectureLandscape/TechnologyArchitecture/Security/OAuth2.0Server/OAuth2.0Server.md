OAuth 2.0 是一个用于**授权（Authorization）**的开放标准协议，而不是认证协议（Authentication）。它允许应用在不暴露用户密码的情况下，安全地访问用户在某个服务提供方（Resource Server）上的资源。下面我给你整理一个详细的概览：

---

## 1. 核心概念

| 名称                              | 作用                 |
| ------------------------------- | ------------------ |
| **Resource Owner**（资源所有者）       | 用户，拥有受保护的资源        |
| **Client**（客户端）                 | 需要访问资源的应用程序        |
| **Authorization Server**（授权服务器） | 负责认证用户身份并发放访问令牌    |
| **Resource Server**（资源服务器）      | 托管用户资源，负责验证访问令牌    |
| **Access Token**（访问令牌）          | 授权客户端访问资源的凭证       |
| **Refresh Token**（刷新令牌）         | 用于获取新的访问令牌（通常长期有效） |

---

## 2. OAuth 2.0 授权流程（常见的四种模式）

### 2.1 授权码模式（Authorization Code Grant）

**适用场景**：Web 应用（服务器端）
**流程**：

1. Client 引导用户到 Authorization Server 授权页面。
2. 用户登录并同意授权。
3. Authorization Server 返回一个 **Authorization Code** 给 Client。
4. Client 使用 Authorization Code 向 Authorization Server 换取 **Access Token**。
5. Client 使用 Access Token 访问 Resource Server 的资源。

特点：安全，Access Token 不暴露在浏览器端。

---

### 2.2 隐式授权模式（Implicit Grant）

**适用场景**：单页应用（SPA）
**流程**：

1. 用户在浏览器端直接获取 **Access Token**。
2. 浏览器端使用 Access Token 访问资源。

特点：不安全（因为 Access Token 暴露在浏览器中），现在不推荐使用。

---

### 2.3 密码模式（Resource Owner Password Credentials Grant）

**适用场景**：受信任的应用（例如企业内部应用）
**流程**：

1. 用户向 Client 提供用户名和密码。
2. Client 向 Authorization Server 请求 Access Token。

特点：用户密码直接交给 Client，不安全，只适合高度信任场景。

---

### 2.4 客户端模式（Client Credentials Grant）

**适用场景**：服务端之间通信
**流程**：

1. Client 使用自身的 Client ID 和 Secret 向 Authorization Server 请求 Access Token。
2. 使用 Access Token 访问 Resource Server。

特点：无用户参与，仅用于服务间授权。

---

## 3. Token 类型

| 类型                | 作用                       |
| ----------------- | ------------------------ |
| **Access Token**  | 短期有效，访问受保护资源             |
| **Refresh Token** | 长期有效，用于获取新的 Access Token |

---

## 4. 安全注意事项

1. **HTTPS 必须**：传输令牌必须加密。
2. **Access Token 不暴露前端**：尤其在授权码模式下。
3. **Scope（权限范围）**：限制 Access Token 的访问权限。
4. **过期策略**：Access Token 有效期短，Refresh Token 用于续期。
5. **避免密码模式**：尽量避免直接收集用户密码。

---

## 5. 常用 OAuth 2.0 扩展协议

1. **OpenID Connect (OIDC)**：在 OAuth 2.0 基础上增加认证功能，实现单点登录（SSO）。
2. **PKCE（Proof Key for Code Exchange）**：对授权码模式增加安全性，防止授权码被窃取。

---

如果你需要，我可以帮你画一张**完整 OAuth 2.0 授权码模式流程图**，把用户、客户端、授权服务器和资源服务器的交互都画出来，看一眼就懂整个流程。

你希望我画吗？
