#include "QuadraticProbing.h"
#include "familytree.h"
#include "familyRunner.h"
#include <cstring>
using namespace std;

QuadraticHashTable::QuadraticHashTable(int size)
	:currentSize(size)
{
     array = new Person2[size];
	for(int i = 0; i < size; i++)
		array[i].parent1 = array[i].parent2 = array[i].person.year = -1;
}


int QuadraticHashTable::insert(Person &x)
{
	int i = strlen(x.firstName);
	memset(&x.firstName[i], 0, 12 - i);
	i = strlen(x.lastName);
	memset(&x.lastName[i], 0, 12 - i);
	((char*)&x)[27] = 0;
	
	unsigned currentPos = 0;
	for(int i = 0; i < 7; i++)
	   currentPos += ((unsigned*)&x)[i];
	
	currentPos %= currentSize;
	if(currentPos < 0)
		currentPos += currentSize;
	
	while (array[currentPos].person.year != -1 && memcmp(&x, &array[currentPos].person, sizeof(Person)) != 0)
	
		{
		if(++currentPos >= currentSize)
			currentPos = 0;

		}
	if (array[currentPos].person.year == -1)
		memcpy(&array[currentPos].person, &x, sizeof(Person));
	return currentPos;

	
}
