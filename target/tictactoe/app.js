const boardEl = document.getElementById("board");
const messageEl = document.getElementById("message");
const newGameBtn = document.getElementById("newGameBtn");

newGameBtn.addEventListener("click", newGame);

async function newGame() {
  const response = await fetch("api/newgame", { method: "POST" });
  const state = await response.json();
  render(state);
}

async function move(cell) {
  const response = await fetch("api/move", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ cell })
  });
  const state = await response.json();
  if (!response.ok) {
    messageEl.textContent = state.error;
    return;
  }
  render(state);
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
