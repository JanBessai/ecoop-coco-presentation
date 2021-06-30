all: clean ECOOP.class
	java ECOOP

ECOOP.class: ECOOP.java
	javac ECOOP.java

.PHONY: clean
clean:
	rm -f *.class


