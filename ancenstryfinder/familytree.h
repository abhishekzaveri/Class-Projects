#ifndef FAMILYTREE_H
#define	FAMILYTREE_H

#include "familyRunner.h"
#include "QuadraticProbing.h"
#include "QueueAr.h"
#include "dsexceptions.h"

class PersonCopy
{
public:
	PersonCopy();
	PersonCopy(Person p): person(p), indexValue(0), indexParent(-1), indexParentSpouse(-1) {};

	Person person;
	int indexValue;//hash function generator
	int indexParent;
	int indexParentSpouse;
	static int compare(const void *a, const void *b);	
};



class FamilyTree {
  
public:
  FamilyTree(Family *families, int familyCount);
  void runQueries(Query *queries, Person *answers, int queryCount);
   
 //member data
QuadraticHashTable <PersonCopy> hashTable;


};

#endif	/* FAMILYTREE_H */

