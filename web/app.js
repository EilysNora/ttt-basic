const BASE_URL = "http://localhost:8000";

const boardEl = document.getElementById("board");
const messageEl = document.getElementById("message");
const newGameBtn = document.getElementById("newGameBtn");

let gameId = null;

newGameBtn.addEventListener("click", newGame);

async function newGame() {
  const response = await fetch(BASE_URL + "/newgame", { method: "POST" });
  const state = await response.json();
  gameId = state.id;
  render(state);
}

async function move(cell) {
  const response = await fetch(`${BASE_URL}/move?id=${gameId}&cell=${cell}`, { method: "POST" });
  const body = await response.json();
  if (!response.ok) {
    messageEl.textContent = body.error;
    return;
  }
  render(body);
}

function render(state) {
  const n = Math.round(Math.sqrt(state.size));
  boardEl.style.gridTemplateColumns = `repeat(${n}, 60px)`;
  boardEl.innerHTML = "";

  const gameOver = state.status !== "IN_PROGRESS";
  state.board.forEach((value, index) => {
    const cell = index + 1;
    const btn = document.createElement("button");
    btn.className = "cell";
    btn.textContent = value === 1 ? "X" : value === 2 ? "O" : "";
    btn.disabled = gameOver || value !== 0;
    btn.addEventListener("click", () => move(cell));
    boardEl.appendChild(btn);
  });

  messageEl.textContent = state.message;
}

newGame();
