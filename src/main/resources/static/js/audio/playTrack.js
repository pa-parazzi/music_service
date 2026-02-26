export function playTrack(soundList, index, playBtn, player){
    const track = soundList[index];
    player.src = track.url;
    player.play();

    document.querySelectorAll('.track').forEach((el, i) => {
        el.classList.toggle('active', i === index);
    });

    playBtn.textContent = "⏸";
    return index;
}