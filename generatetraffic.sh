#!/bin/bash

# Set initial counter values
get_task_counter=1
update_status_true_counter=21000
update_status_false_counter=1
update_task_description_counter=21000
delete_task_counter=6900

# Function to generate a random number between 1 and 1000
rand() {
    echo $((RANDOM % 1000 + 1))
}

# Function to generate traffic
generate_traffic() {
    while true; do
        r=$(rand)

        if (( r <= 600 )); then
            # GET TASK WITH ID (60% chance)
            curl -s "http://localhost:8080/tasks/$get_task_counter" > /dev/null
            ((get_task_counter++))
            if [ "$get_task_counter" -gt 21000 ]; then
                get_task_counter=1
            fi
        elif (( r <= 605 )); then
            # GET LIST OF COMPLETED TASKS (0.5% chance)
            curl -s -X GET "http://localhost:8080/tasks/completed" > /dev/null
        elif (( r <= 610 )); then
            # GET LIST OF UNCOMPLETED TASKS (0.5% chance)
            curl -s -X GET "http://localhost:8080/tasks/uncompleted" > /dev/null
        elif (( r <= 700 )); then
            # CREATE TASKS WITH POST METHOD (9% chance)
            month=$(printf "%02d" $((RANDOM % 12 + 1)))
            day=$(printf "%02d" $((RANDOM % 28 + 1)))
            random_date="2023-${month}-${day}"

            json_body="{\"description\":\"description${RANDOM}\",\"dueDate\":\"${random_date}\",\"isCompleted\":false}"

            curl -s -X POST "http://localhost:8080/tasks/" \
                 -H "Content-Type: application/json" \
                 -d "$json_body" > /dev/null
        elif (( r <= 750 )); then
            # UPDATE STATUS TO TRUE (5% chance)
            curl -s -X PUT "http://localhost:8080/tasks/${update_status_true_counter}/completion?isCompleted=true" > /dev/null
            ((update_status_true_counter--))
            if [ $update_status_true_counter -lt 1 ]; then
                update_status_true_counter=21000
            fi
        elif (( r <= 800 )); then
            # UPDATE STATUS TO FALSE (5% chance)
            curl -s -X PUT "http://localhost:8080/tasks/${update_status_false_counter}/completion?isCompleted=false" > /dev/null
            ((update_status_false_counter++))
            if [ "$update_status_false_counter" -gt 21000 ]; then
                update_status_false_counter=1
            fi
        elif (( r <= 850 )); then
            # UPDATE TASK DESCRIPTION (5% chance)
            json_body="description${update_task_description_counter}"
            curl -s -X PUT "http://localhost:8080/tasks/${update_task_description_counter}/description" \
                 -H "Content-Type: application/json" \
                 -d "$json_body" > /dev/null
            ((update_task_description_counter--))
            if [ $update_task_description_counter -lt 1 ]; then
                update_task_description_counter=21000
            fi
        elif (( r <= 860 )); then
            # DELETE TASKS (1% chance)
            curl -s -X DELETE "http://localhost:8080/tasks/${delete_task_counter}" > /dev/null
            ((delete_task_counter++))
            if [ "$delete_task_counter" -gt 6999 ]; then
                delete_task_counter=6900
            fi
        else
            # GET TASK WITH ID for the rest (14% chance)
            curl -s "http://localhost:8080/tasks/$get_task_counter" > /dev/null
            ((get_task_counter++))
            if [ "$get_task_counter" -gt 21000 ]; then
                get_task_counter=1
            fi
        fi

        # Pause before the next operation
        sleep 0.1
    done
}

# Start generating traffic
generate_traffic
