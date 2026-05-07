export function loadRegistrationForm(container){
    container.innerHTML = `
  <div class="form-wrapper">
    <form class="registration-form" enctype="multipart/form-data">
        <label for="username">Имя пользователя: </label>
        <input type="text" name="username" class="username" required>
        <div class="username-error"></div>
        <br/>

        <label for="password">Пароль: </label>
        <input type="password" name="password" class="password" required>
        <div class="password-error"></div>
        <br/>

        <label for="email">Почта: </label>
        <input type="email" name="email" class="email" required>
        <div class="email-error"></div>
        <br/>

        <label for="date-of-birth">Дата рождения: </label>
        <input type="text" name="date-of-birth" class="date-of-birth" required>
        <div class="date-of-birth-error"></div>
        <br/>

        <label for="avatar">Аватар: </label>
        <input type="file" name="avatar" class="avatar" accept="image/*">
        <div class="avatar-error"></div>

        <div class="message success-message"></div>
        <div class="message error-message"></div>
        <br/>

        <button type="submit">Зарегистрироваться</button>
    </form>
  </div>`;
}