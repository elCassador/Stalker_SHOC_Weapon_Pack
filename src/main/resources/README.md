{
"Icon": "Icons/ItemsGenerated/Obrez.png",
"Model": "Items/Obrez/Obrez.blockymodel",
"Texture": "Items/Obrez/Obrez_Texture.png",
"IconProperties": {
"Scale": 0.15,
"Rotation": [
315,
90,
0
],
"Translation": [
-2,
-67
]
},
"Recipe": {
"TimeSeconds": 3.5,
"Input": [
{
"ItemId": "Ingredient_Bar_Iron",
"Quantity": 15
},
{
"ItemId": "Ingredient_Bar_Copper",
"Quantity": 25
},
{
"ResourceTypeId": "Wood_Trunk",
"Quantity": 10
}
],
"BenchRequirement": [
{
"Id": "Weapon_Bench",
"RequiredTierLevel": 2,
"Categories": [
"Weapon_Bow"
]
}
]
},
"MaxStack": 1,
"Scale": 0.04,
"Parent": "Weapon_Handgun",
"PlayerAnimationsId": "Rifle",
"Categories": [
"StalkerGunsCategory.Guns"
],
"Quality": "Technical",
"TranslationProperties": {
"Name": "Obrez"
},
"Utility": {
"Compatible": true
},
"Interactions": {
"Primary": {
"Cooldown": {
"Cooldown": 0.4
},
"RequireNewClick": false,
"Interactions": [
{
"Type": "Shoot",
"Failed": "Gun_Shoot_Fail",
"Effects": {
"ItemAnimationId": "Shoot",
"FirstPersonParticles": [
{
"SystemId": "RifleShooting",
"PositionOffset": {
"X": -0.2,
"Z": 3,
"Y": 0.2
},
"RotationOffset": {
"Yaw": 0,
"Pitch": 90
}
}
],
"Particles": [
{
"SystemId": "RifleShooting",
"RotationOffset": {
"Pitch": 90
},
"PositionOffset": {
"X": -0.2,
"Z": 2.4,
"Y": 0.2
}
}
],
"CameraEffect": "Handgun_Shoot"
},
"Damage": 1,
"Pellets": 7,
"Spread": 5,
"ShootSound": "Obrez_Shoot"
}
]
},
"Ability3": {
"Cooldown": {
"Cooldown": 3.3
},
"Interactions": [
{
"Type": "Overload_Check",
"MaxAmmo": 2,
"Next": {
"Type": "Charging",
"HorizontalSpeedMultiplier": 0.4,
"Effects": {
"WorldSoundEventId": "Toz_Reload",
"LocalSoundEventId": "Toz_Reload"
},
"MaxAmmo": 2,
"Next": {
"2.4": {
"Type": "Load_Magazine",
"MaxAmmo": 2,
"AmmoItemType": "Ammo_S12",
"Effects": {
"ItemAnimationId": "Interact"
}
}
}
}
}
]
}
},
"MaxDurability": 3000
}

obrez with charge.

server.benchCategories.stalker.guns – название из .lang файла