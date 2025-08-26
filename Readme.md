MobSoccer (Minecraft 1.20)
## Table of Contents
[Summary](#Summary)  
[Installation](#Installation)  
[Commands](#Commands)  
[Configuration](#Configuration)  
[Gameplay](#gameplay)  
[Permissions](#permissions)  
[Messages & Placeholders](#messages--placeholders)  

## Summary
MobSoccer is a minigame where hostile mobs (“the ball”) spawn at a set point and players use a knockback stick (“Spoon!”) to whack the mob into their team’s goal. Teams score points when the mob enters their goal area. A text-display scoreboard shows team scores in-world.

## Installation
1. Build the plugin
```
git clone https://github.com/extstudios/MobSoccer-1.20.git
cd MobSoccer-1.20
mvn clean package
```
The project is a Maven build targeting Java 16 and depending on spigot-api:1.20-R0.1-SNAPSHOT (scope provided).
2. Drop into your server
   - Place the built JAR in your server’s plugins/ folder.
   - The plugin declares api-version: '1.20'. 

## Commands
All commands are under the base command /ms (alias: /mobsoccer). Usage and behavior below

 Command                                 | What it does                                                                 
-----------------------------------------|------------------------------------------------------------------------------
 /ms (no args)                           | Prints a help list for all subcommands.                                      
 /ms start                               | Starts the game after required locations are set (see Gameplay).             
 /ms cancel	                             | Cancels the running game.                                                    
 /ms spoon {player}                      | Gives the caller (or a named player) a Knockback II stick named “Spoon!”.    
 /ms setgoal {team}                      | Sets the goal location for a team to your current position.                  
 /ms setteamstart {team}                 | Sets the team’s start/teleport location to your current position.            
 /ms setteamexit		                 | Sets the common post-game exit location to your current position.            
 /ms setmobspawn                         | Sets the mob (“ball”) spawn location to your current position.               
 /ms setpotionarea		                 | Defines the center of a square zone where potions can drop during the match. 
 /ms setteam {player} {team}             | Adds the player to the named team.                                           
 /ms removeteam {player} {team}          | Removes the player from the named team. (Usage string exists; see Notes.)    
 /ms clearteams                          | Clears all players from all teams.                                           
 /ms teams		                         | Lists current team names in chat.                                            
 /ms changeteam {team} {newName} {color} | Renames a team and (optionally) changes its color.                           
 /ms setscoreboardlocation		         | Creates/places the text-display scoreboard at your current position.         
 /ms rotatescoreboard		             | Rotates the text-display scoreboard (cycles through preset rotations).       

Base command + alias: Declared in plugin.yml as ms with alias mobsoccer. 

## Configuration
```
config.yml
world: 'world'
scoreboardSize: 13
entityTypes:
- ZOMBIE
- SKELETON
- STRAY
- WITCH
- PIGLIN
- PIGLIN_BRUTE
- ZOMBIFIED_PIGLIN
- ZOMBIE_VILLAGER
- HUSK
```
  - world: World name used by the plugin.
  - scoreboardSize: Scale multiplier for the in-world text display.
  - entityTypes: Set of mob types that can spawn as the “ball.” 
  - Locations (auto-saved)
  You set locations via commands; the plugin persists them to locations.yml (created automatically). Keys include goal locations (per team), team start locations (per team), mob spawn, team exit, and potion spawn area. Manual editing isn’t required. 

## Gameplay
- Teams: On enable, the plugin (re)creates Team1, Team2, Team3, Team4 with colors GREEN, GOLD, RED, and DARK_PURPLE.
- Prerequisites to start: /ms start checks that mob spawn, ≥4 goal locations, ≥4 team start locations, and a team exit are set; otherwise it refuses to start.
- Countdown & equips: A 5-second broadcast countdown runs, players are teleported to their team starts, and each online team member receives a Spoon! stick and a leather “Jersey” chestplate tinted to the team color. 
- Spawning the “ball”: Every ~10 seconds, the plugin spawns 3 mobs at the mob spawn location, each randomly chosen from config.yml’s entityTypes. Each mob is named “ball” and is treated as the soccer ball. 
- Scoring: When the “ball” enters a team’s goal region (a small box around the saved point), that team’s score increments, a sound plays, fireworks launch in the team’s color, and the scoreboard text updates. 
- Win condition: A team wins at 10 points (not configurable in this repo). On win, players are teleported to the exit, fireworks celebrate, spoons/jerseys are removed, and the game is stopped. 
- Optional potions: If a potion area is set, splash potions (Speed/Slowness) drop periodically within a small square centered at that area. 
- Scoreboard: The in-world text scoreboard shows “TeamName: Score” per team and can be rotated via /ms rotatescoreboard. Scale is controlled by scoreboardSize.

## Permissions
Declared in plugin.yml (all default to op). There’s also a wildcard node with children. 
Permission	Purpose (from plugin.yml)

## Messages & Placeholders
messages.yml contains three sections: messages.error, messages.commands, and messages.broadcast. Strings support & color codes and use simple placeholders:
- %player%, %team%, %seconds%.
Example keys (as shipped):
- Errors like unknownCommand, noPermission, gameInProgress, etc.
- Command messages like goalLocationSet, mobSpawnLocationSet, teamNameChanged, etc.
- Broadcasts like gameStarting, gameStart, gameCancel, gameOver, teamScored.
