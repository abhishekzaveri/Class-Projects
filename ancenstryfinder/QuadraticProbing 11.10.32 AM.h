:        #ifndef _QUADRATIC_PROBING_H_
        #define _QUADRATIC_PROBING_H_
	#include "familyRunner.h"
        

        // QuadraticProbing Hash table class
        //
        // CONSTRUCTION: an initialization for ITEM_NOT_FOUND
        //               and an approximate initial size or default of 101
        //
        // ******************PUBLIC OPERATIONS*********************
        // void insert( x )       --> Insert x
        // void remove( x )       --> Remove x
        // Hashable find( x )     --> Return item that matches x
        // void makeEmpty( )      --> Remove all items
        // int hash( String str, int tableSize )
        //                        --> Static method to hash strings


	class Person2
	{
		public:
			Person person;
			int parent1;	
			int parent2;
	};
	

	class QuadraticHashTable
	{
		public:
			explicit QuadraticHashTable(int size);
			int insert(Person &x);
			Person2 *array;
			unsigned int currentSize;
	};


	#endif
