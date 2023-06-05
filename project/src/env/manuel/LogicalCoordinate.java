package manuel;

import jason.environment.grid.Location;

import static manuel.Env.LANE_LENGTH;
import static manuel.Env.SIZE;

public class LogicalCoordinate {
	public final int side;
	public final int lane;
	public final int distance;

	private LogicalCoordinate(int side, int lane, int distance) {
		this.side = side;
		this.lane = lane;
		this.distance = distance;
	}

	public static LogicalCoordinate of(int side, int lane, int distance) {
		return new LogicalCoordinate(side, lane, distance);
	}

	public static LogicalCoordinate of(int x, int y) {
		if (x < LANE_LENGTH || x >= (LANE_LENGTH + 6)) {
			if (y < (LANE_LENGTH + 3))
				return new LogicalCoordinate(1, Math.abs(y - (LANE_LENGTH + 2)), SIZE - x);
			else
				return new LogicalCoordinate(3, y - (LANE_LENGTH + 3), x);
		}
		if (x < (LANE_LENGTH + 3))
			return new LogicalCoordinate(0, x - LANE_LENGTH, y);
		else
			return new LogicalCoordinate(2, x - LANE_LENGTH - 3, SIZE - y);
	}

	public static LogicalCoordinate of(Location location) {
		return LogicalCoordinate.of(location.x, location.y);
	}

}
