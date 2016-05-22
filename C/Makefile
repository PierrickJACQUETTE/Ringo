CC 	  = gcc
CFLAGS  = -Wall
EXEC 	  = RINGO

HEADERS = $(wildcard *.h)
OBJECTS = $(patsubst %.c, %.o, $(wildcard *.c))

all: $(EXEC)

RINGO: $(OBJECTS)
	$(CC) $(CFLAGS) -o $@ $^

%.o: %.c $(HEADERS)
	$(CC) $(CFLAGS) -c -o $@ $<

detailed: CFLAGS += -DDETAILED

detailed: all

clean:
	rm -f *.o

mrproper: clean
	rm -f $(EXEC)
	rm -f *.gch
