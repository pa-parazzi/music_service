export function playSound(playSoundBtn, player, playBtn, sound){
    playSoundBtn.addEventListener('click', () => {
        if (!player.src) {
            player.src = sound.url;
            player.play();
            playBtn.textContent = "⏸";
        } else if (player.paused) {
            player.play();
        } else {
            player.pause();
        }
    });

    player.addEventListener('play', () => {
        playSoundBtn.textContent = "⏸";
        playBtn.textContent = "⏸";
    });

    player.addEventListener('pause', () => {
        playSoundBtn.textContent = "▶";
        playBtn.textContent = "▶";
    });
}