# Test Maven Project 

This is the source code used for the Maven project, which is used for testing Hadoop Integration with Elasticsearch

## Compile 

1. Install Maven:

    ```bash
    sudo apt install -y maven 
    ```

2. Build the JAR file.

    ```bash
    mvn clean package 
    ```

3. If successful, you should see a build success message:

    ```bash
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  36.095 s
    [INFO] Finished at: 2025-01-05T17:03:43+08:00
    [INFO] ------------------------------------------------------------------------
    ```