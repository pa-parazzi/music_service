export function loadLoginForm(container){
    container.innerHTML = `
    <div class="form-wrapper">
      <form class="login-form">
        <label for="username">Имя пользователя: </label>
        <input type="text" name="username" class="username">
        <div class="username-error"></div>

        <label for="password">Пароль: </label>
        <input type="password" name="password" class="password">
        <div class="password-error"></div>

        <div class="message error-message"></div>
        <div class="message success-message"></div>

        <button type="submit">Войти</button>
      </form>
    </div>`;
}