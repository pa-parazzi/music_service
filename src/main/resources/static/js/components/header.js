export function loadHeader(container){
    container.innerHTML = `
    <header class="header">
      <div class="header-left">
          <button class="sidebar-open-btn">&#9776;</button>
          <a href="/main" class="logo">&#8962;</a>
      </div>

      <form class="search-form">
          <button class="search-button" type="submit">&#128270;</button>
          <input class="search-input" type="text" placeholder="Поиск">
      </form>

      <nav class="auth-buttons">
          <a href="/auth/login" class="sign-in-button">Sign in</a>
          <a href="/auth/registration" class="sign-up-button">Sign up</a>
      </nav>
      <div class="user-profile"></div>
    </header>`;
    return container.querySelector(".header");
}