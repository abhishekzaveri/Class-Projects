#ifndef FAMILYTREE_H
#define	FAMILYTREE_H

#include "familyRunner.h"
#include "QuadraticProbing.h"


class YearIndex
{
public:
	short year;
	int index;
	YearIndex(short y=0, int i =0): year(y), index(i) {}
	
		
};



class FamilyTree {
	QuadraticHashTable table;  
public:
  FamilyTree(Family *families, int familyCount);
  void runQueries(Query *queries, Person *answers, int queryCount);
   



};

#endif	/* FAMILYTREE_H */

