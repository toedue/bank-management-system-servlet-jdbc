<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Bank - Login</title>
    <style>
      * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
      }

      body {
        font-family: Arial, Helvetica, sans-serif;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #1e3c72, #2a5298);
        color: #222;
      }

      .auth-wrapper {
        width: 100%;
        max-width: 400px;
        background: #ffffff;
        border-radius: 8px;
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        padding: 30px 28px 26px;
      }

      .auth-header {
        text-align: center;
        margin-bottom: 18px;
      }

      .auth-title {
        font-size: 1.6rem;
        margin-bottom: 4px;
        color: #1e3c72;
      }

      .auth-subtitle {
        font-size: 0.9rem;
        color: #666;
      }

      .form-group {
        margin-top: 14px;
      }

      label {
        display: block;
        margin-bottom: 4px;
        font-size: 0.9rem;
        color: #444;
      }

      input[type="text"],
      input[type="password"] {
        width: 100%;
        padding: 9px 10px;
        border-radius: 4px;
        border: 1px solid #ccd2e3;
        font-size: 0.95rem;
      }

      input[type="text"]:focus,
      input[type="password"]:focus {
        outline: none;
        border-color: #2a5298;
        box-shadow: 0 0 0 2px rgba(42, 82, 152, 0.15);
      }

      .btn-primary {
        width: 100%;
        margin-top: 18px;
        padding: 10px 0;
        border-radius: 4px;
        border: none;
        background: #2a5298;
        color: #ffffff;
        font-size: 1rem;
        cursor: pointer;
      }

      .btn-primary:hover {
        background: #23457f;
      }

      .error {
        color: #c0392b;
        margin-top: 10px;
        font-size: 0.9rem;
        min-height: 18px;
        text-align: center;
      }

      .helper-text {
        text-align: center;
        margin-top: 14px;
        font-size: 0.85rem;
      }

      .helper-text span {
        font-weight: bold;
      }
    </style>
  </head>
  <body>
    <div class="auth-wrapper">
      <div class="auth-header">
        <div class="auth-title">Welcome</div>
        <div class="auth-subtitle">Sign in to your account</div>
      </div>

      <form action="login" method="post">
        <div class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            type="text"
            name="email"
            required
            autocomplete="off"
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input id="password" type="password" name="password" required />
        </div>

        <button class="btn-primary" type="submit">Login</button>
      </form>

      <p class="error"></p>
    </div>
  </body>
</html>
