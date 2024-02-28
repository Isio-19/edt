all: 
	mvn clean javafx:run

clean: 
	rm src/main/resources/isio/json/*
	rm src/main/resources/isio/raw/*