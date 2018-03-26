# ExerciseInterpreter
Version: 1.0.0  
Classification: Interpreter

Overview
-----
The ExerciseInterpreter is a plugin which interprets activity data by filtering activities into different activity types.

Configuration
-----
The ExerciseInterpreter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| activityThreshold | 10 | The activity threshold that needs to be exceeded in order to add an entry to the list of return values during interpretation. | 
| activitySliceThreshold | 10 | The maximum duration of a slice, in minutes. |

Required Plugins
-----
 - The ExerciseInterpreter does not require any other plugins.