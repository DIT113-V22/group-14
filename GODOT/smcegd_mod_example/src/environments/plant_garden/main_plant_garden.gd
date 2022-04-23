extends Spatial

export var max_balls: int = 20

"""func _ready() -> void:
	var timer: Timer = Timer.new()
	timer.wait_time = 2
	add_child(timer)
	timer.connect("timeout", self, "_on_timeout")
	timer.start()

var balls: Array = []
"""
"""func _on_timeout() -> void:
	var ball: RigidBody = preload("res://src/environments/example/square.tscn").instance()
	add_child(ball)

	var rand_vec: Vector3 = Vector3(rand_range(0,10),rand_range(0,10),rand_range(0,10))
	ball.apply_central_impulse(rand_vec)
	ball.apply_torque_impulse(rand_vec)
	
	balls.push_back(ball)
	if balls.size() > max_balls:
		balls.pop_front().queue_free()
"""

func init_cam_pos() -> Basis:
	return $Camera.global_transform

func get_spawn_position(hint: String) -> Transform:
	match hint:
		"debug_vehicle": return $DebugVehicleSpawn.global_transform
		_: return $VehicleSpawn.global_transform
	

