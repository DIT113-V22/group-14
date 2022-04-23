extends Reference

var mod_name: String = "example"

func init(global) -> void:
	global.register_environment("plant_garden/main_plant_garden", load("res://src/environments/plant_garden/main_plant_garden.tscn"))
	global.register_vehicle("StupidCar", load("res://src/vehicles/stupid_car/StupidCar.tscn"))
	print("Hello World!")
