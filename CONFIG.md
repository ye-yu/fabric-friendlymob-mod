# Mod Configuration File

Configuration file is temporary! This will be removed
after the first complete release.

The configuration file can be found in `mods/Befriend Minecraft/config.json`.
However, this file will not appear unless the mod has been loaded at least once.

On every minor update, the mod may overwrite this config file. 
If the configuration field is invalid, the mod will also overwrites the config file too.

This will be fixed in the future update.

### Configuration Details

| Field name | Field type | Description | Default value |
|--------|---------|---|---|
| wonder_space | integer | Specifies the number of pre-allocated space for wonder-traded items | 5 |
| simulate_multiplayer | integer | Enables random wonder-trade return item regardless of uploaded items | true |
| random_message_list | string | Specifies the file that contains random lines when simulating multiplayer | randomMessage.txt |
| vindor_transform_chance | float | Specifies the chance to transform a Vindicator to a Vindor | 0.01 |
| evione_max_spell_tick | integer | Specifies the duration of speeding up of synthesis | 70 |
| evione_synthesis_chance | float | Specifies the chance of synthesis without spell casting | 0.1 |
| evione_synthesis_can_speed_up_chance | float | Specifies the chance of performing spell casting | 0.005 |
| evione_synthesis_speed_up_count | integer | Specifies the number of extra rolls to synthesis item when spell casting | 6 |
| evione_synthesis_speed_up_chance | float | Specifies the chance of success for each roll of spell casting | 0.003 |
| evione_transform_chance | float | Specifies the chance of transforming an Evoker to an Evione | 0.23 |
| evione_drop_vex_essence_chance | float | Specifies the chance of an Evione to drop a Vex Essence | 0.002 |
| vex_essence_caught_chance | float | Specifies the chance of getting a Vex essence from fishing an Evoker | 0.42 |



### Wonder Trading

When `simulate_multiplayer` is set to `true`, then whatever items that is uploaded by the Vindor
is replaced with a random item with random item count bounded by their max count. The attached
message is selected from the `random_message_list` file.

### Fishing an Evoker

An evoker can be fished to steal their Vex essence stock. For each successful roll of 
`vex_essence_caught_chance`, the mod rolls another chance from `evione_transform_chance` to transform the
evoker. This makes the true chance to transform an Evoker to an Evione to become
`vex_essence_caught_chance` x `evione_transform_chance`.

### Synthesis Spell-casting

For each tick, the mod rolls for `evione_synthesis_chance` and increments the synthesis progress
by one for each successful roll. 

In the meantime, if Evione is not spell-casting, the mod will always
activates spell casting if Vex essence is supplied. Otherwise, 
the mod also roll for  `evione_synthesis_can_speed_up_chance` and 
starts spell casting if the roll is successful.

After enabling spell casting, the mod will roll for 
`evione_synthesis_speed_up_chance` for `evione_synthesis_speed_up_count`
times. For each successful rolls, the synthesis progress is incremented by one.
