START_MAPPING = {
    1: 'F',
    2: 'F'
}
START_DEFAULT = '|'
NORTH = (-1, 0)
SOUTH = (1, 0)
EAST = (0, 1)
WEST = (0, -1)
CONNECTION_MAPPING = {
    '|': [NORTH, SOUTH],
    '-': [EAST, WEST],
    'L': [NORTH, EAST],
    'J': [NORTH, WEST],
    '7': [SOUTH, WEST],
    'F': [SOUTH, EAST]
}


def parse_input(path, test=None):
    start = None
    connections = {}
    start_tile = START_MAPPING.get(test, START_DEFAULT)
    with open(path) as f:
        for row, line in enumerate(f.readlines()):
            for column, tile in enumerate(line.strip()):
                position = (row, column )
                if tile == 'S':
                    start = position
                    tile = start_tile
                tile_connection = CONNECTION_MAPPING.get(tile)
                if tile_connection:
                    connections[position] = tile_connection
    return start, connections


class MazeRunner():

    def __init__(self, start, connections):
        self.start = start
        self.connections = connections
        self.loop = []

    @staticmethod
    def get_position(position, connection):
        return (position[0] + connection[0], position[1] + connection[1])
    
    def get_candidate_positions(self, position):
        position_connections = self.connections.get(position)
        return [self.get_position(position, x) for x in position_connections]

    def get_connected_tiles(self, position):
        connected_tiles = []
        candidates = self.get_candidate_positions(position)
        for candidate in candidates:
            candidate_connections = self.get_candidate_positions(candidate)
            if position in candidate_connections:
                connected_tiles.append(candidate)
        return connected_tiles
    
    def get_next_tile(self):
        connected_tiles = [x for x in self.get_connected_tiles(self.loop[-1]) if x not in self.loop]
        if connected_tiles:
            return connected_tiles[0]

    def get_main_loop(self):
        self.loop = [self.start]
        while True:
            next_tile = self.get_next_tile()
            if not next_tile:
                break
            self.loop.append(next_tile)
