# webapp

 
 Git Commands 
 git clone <File-name.git>
 Open the file and Commands to build the Application:
 mvn clean install

 Push your cahnges to the forked repo's Branch
 Then Create a Pull Request to Organization Main
 Sync with the main to forked main

 For Databases
 Start Postgres: brew services start postgres

 Postman
 http://localhost:8080/v1/assignments 

 Give Get, Post, Put, Delete works as expected
 Get - Get teh Assignments
 Post- Create Assignment
 Put - Update Assignment({/id})
 Delete- Delete Assignment(/{id})
 For Patch 405 Method Not Found

 ReadMe Update for demo

 command to import the certificate from Local to AWS Certificate Manager: 
aws acm import-certificate --profile demo
 --certificate fileb://demo_cjoshi_tech.crt
 --private-key fileb://private.key
