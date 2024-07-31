delete_task_counter=6900

while true; do
    # DELETE TASKS
    curl -s -X DELETE "http://localhost:8080/tasks/${delete_task_counter}" > /dev/null
    ((delete_task_counter++))
    if [ "$delete_task_counter" -gt 6999 ]; then
        delete_task_counter=6900
    fi
    sleep 0.2
done

