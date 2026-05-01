export function renderUserProfile(userProfileContainer, user){
    userProfileContainer.innerHTML = `
              <button class="avatar-btn">
                   <img src="${user.avatar?.url}" alt="avatar">
              </button>
              
              <div class="profile-drop-down">
                   <div class="profile-name">${user.username}</div>
                   <button class="logout-btn">Выйти</button>             
              </div>`;
}