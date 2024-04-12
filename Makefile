all: 
	mvn clean javafx:run
	clear
	
clean: 
	rm src/main/resources/isio/json/*
	rm src/main/resources/isio/raw/*