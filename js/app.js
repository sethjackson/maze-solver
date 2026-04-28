const TileType = Object.freeze({
  START: 'S',
  END: 'E',
  SPACE: '.',
  WALL: '#',

  toTileType: function(c) {
    switch (c) {
      case 'S':
        return TileType.START;
      case 'E':
        return TileType.END;
      case '.':
        return TileType.SPACE;
      case '#':
        return TileType.WALL;
    }

    return null;
  }
});

class Tile {
  constructor(x, y, tileType) {
    this.x = x;
    this.y = y;
    this.tileType = tileType;
    this.visited = false;
  }
}

class Maze {
  #width;

  constructor(tiles, start, longestLineLength) {
    this.tiles = tiles;
    this.start = start;
    this.history = [];
    this.#width = longestLineLength;
  }

  static parse(maze) {
    let tiles = [];
    let start = null;
    let longestLineLength = 0;
    let y = 0;

    for (const line of maze.split('\n').filter(Boolean)) {
      longestLineLength = Math.max(line.length, longestLineLength);

      const row = [];
      for (let x = 0; x < line.length; x++) {
        const c = line.charAt(x);

        const tileType = TileType.toTileType(c);
        if (!tileType) {
          throw new Error(`Invalid tile type '${c}'.`);
        }

        const tile = new Tile(x, y, tileType);
        if (tile.tileType === TileType.START) {
          if (start) {
            throw new Error('Only one starting point allowed.');
          }

          start = tile;
        }

        row.push(tile);
      }

      tiles.push(row);

      y++;
    }

    return new Maze(tiles, start, longestLineLength);
  }

  solve() {
    if (!this.start) {
      return false;
    }

    this.history = [];

    for (const column of this.tiles) {
      for (const tile of column) {
        tile.visited = false;
      }
    }

    const moves = [];
    moves.push(this.start);

    while (moves.length > 0) {
      const tile = moves.pop();

      if (tile.tileType === TileType.END) {
        this.history.push(tile);

        return true;
      } else if (tile.tileType === TileType.WALL) {
        continue;
      } else if (tile.visited) {
        continue;
      }

      tile.visited = true;

      this.history.push(tile);

      const x = tile.x;
      const y = tile.y;

      if (x < this.width() - 1) {
        moves.push(this.tiles[y][x + 1]);
      }

      if (y > 0) {
        moves.push(this.tiles[y - 1][x]);
      }

      if (x > 0) {
        moves.push(this.tiles[y][x - 1]);
      }

      if (y < this.height() - 1) {
        moves.push(this.tiles[y + 1][x]);
      }
    }

    return false;
  }

  width() {
    return this.#width;
  }

  height() {
    return this.tiles.length;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('load-maze-button').addEventListener('click', loadMaze);
  document.getElementById('animate-button').addEventListener('click', animate);
  document.getElementById('step-button').addEventListener('click', step);
  document.getElementById('pause-button').addEventListener('click', pause);
});

function appendAlert(element, type, message) {
  const wrapper = document.createElement('div');
  wrapper.innerHTML = [
    `<div class="alert alert-${type} alert-dismissible" role="alert">`,
    `   <div>${message}</div>`,
    '   <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>',
    '</div>'
  ].join('');

  element.append(wrapper);
}

async function loadMaze() {
  const alertElement = document.getElementById('alert-message');

  const file = document.getElementById('file').files[0];
  if (!file) {
    appendAlert(alertElement, 'info', 'Please select a file.');

    return;
  }

  const animateButton = document.getElementById('animate-button');
  const stepButton = document.getElementById('step-button');
  const pauseButton = document.getElementById('pause-button');

  try {
    const maze = Maze.parse(await file.text());

    const modal = bootstrap.Modal.getInstance(document.getElementById('load-maze-modal'));
    if (modal) {
      modal.hide();
    }

    const start = performance.now();
    const solved = maze.solve();
    const end = performance.now();

    if (solved) {
      animateButton.disabled = false;
      stepButton.disabled = false;
      pauseButton.disabled = true;

      document.getElementById('status').innerHTML = `<b>Solved in:</b> ${(end - start).toFixed(2)} ms, <b>Steps taken:</b> ${maze.history.length}`;
      document.getElementById('step').innerHTML = '';
    } else {
      animateButton.disabled = true;
      stepButton.disabled = true;
      pauseButton.disabled = true;

      document.getElementById('status').innerHTML = `Unsolvable`;
      document.getElementById('step').innerHTML = '';
    }

    globalThis.maze = maze;
    globalThis.step = null;

    document.getElementById('file').value = null;

    draw();
  } catch (error) {
    appendAlert(alertElement, 'danger', error.message);
  }
}

function draw() {
  const maze = globalThis.maze;
  if (!maze) {
    return;
  }

  const canvas = document.getElementById('maze');
  const ctx = canvas.getContext('2d');

  ctx.fillStyle = getComputedStyle(document.documentElement).getPropertyValue('--bs-dark');
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  const tileWidth = Math.trunc(canvas.width / maze.width());
  const tileHeight = Math.trunc(canvas.height / maze.height());

  for (const column of maze.tiles) {
    for (const tile of column) {
      ctx.fillStyle = getFillStyleForTile(tile);
      ctx.fillRect(tile.x * tileWidth, tile.y * tileHeight, tileWidth, tileHeight);
    }
  }

  if (!globalThis.step) {
    return;
  }

  for (let i = 0; i < globalThis.step; i++) {
    const tile = maze.history[i];

    ctx.fillStyle = getComputedStyle(document.documentElement).getPropertyValue('--bs-success');
    ctx.fillRect(tile.x * tileWidth, tile.y * tileHeight, tileWidth, tileHeight);
  }
}

function getFillStyleForTile(tile) {
  switch (tile.tileType) {
    case TileType.START:
      return getComputedStyle(document.documentElement).getPropertyValue('--bs-warning');
    case TileType.END:
      return getComputedStyle(document.documentElement).getPropertyValue('--bs-danger');
    case TileType.SPACE:
      return 'gray';
  }

  return getComputedStyle(document.documentElement).getPropertyValue('--bs-dark');
}

function animate() {
  document.getElementById('animate-button').disabled = true;
  document.getElementById('step-button').disabled = true;
  document.getElementById('pause-button').disabled = false;

  globalThis.tickId = setInterval(tick, 150);
}

function step() {
  document.getElementById('pause-button').disabled = true;

  increment();

  if (hasFinishedMaze()) {
    document.getElementById('animate-button').disabled = true;
    document.getElementById('step-button').disabled = true;
  }
}

function pause() {
  document.getElementById('animate-button').disabled = false;
  document.getElementById('step-button').disabled = false;
  document.getElementById('pause-button').disabled = true;

  clearInterval(globalThis.tickId);
}

function increment() {
   if (globalThis.step < globalThis.maze.history.length) {
    globalThis.step++;

    document.getElementById('step').innerHTML = `<b>Step:</b> ${globalThis.step}`;

    draw();
  }
}

function tick() {
  increment();

  if (hasFinishedMaze()) {
    document.getElementById('step-button').disabled = true;
    document.getElementById('pause-button').disabled = true;

    clearInterval(globalThis.tickId);
  }
}

function hasFinishedMaze() {
  return globalThis.step === globalThis.maze.history.length;
}
