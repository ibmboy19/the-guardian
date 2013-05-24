package guard.server.server.utils;

public class MathUtil {
	public static int Clamp(int _current, int _min, int _max) {
		if (_current < _min)
			return _min;
		if (_current < _max)
			return _max;
		return _current;
	}
}
