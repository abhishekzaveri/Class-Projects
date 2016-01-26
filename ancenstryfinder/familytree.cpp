

#include "familytree.h"
#include <string.h>
#include <stdlib.h>
#include <cstring>
#include "QueueAr.h"
#include "dsexceptions.h"

FamilyTree::FamilyTree(Family *families, int familyCount):hashTable(PersonCopy(), 100000)
{
	int index, childIndex = 0;
	for (int i = 0; i < familyCount; i++)
	{
		PersonCopy person1(families[i].person);
		index = hashTable.insert(person1, familyCount);
	//	std::cout<<index<<std::endl;
		person1.indexValue = index;
	//	cout<<person.person.firstName<<endl;
	//	cout << hashTable.array[index].element.person.firstName<<endl;
		//cout << families[i].childCount << endl; 
		if (families[i].childCount != 0)
		{	

			//cout << "if statement" << endl; 
			for (int k = 0; k < families[i].childCount; k++)
			{
				//cout << "here" << endl; 
				childIndex = hashTable.findPos(families[i].children[k]);
				// call hash function to find index of child,
				// then access the array at that index
				// set that child's parent index to returned index
				
				//cout << childIndex << endl; 
				if(hashTable.array[childIndex].element.indexParent == -1)
				        hashTable.array[childIndex].element.indexParent = index;	

				else
					hashTable.array[childIndex].element.indexParentSpouse = index;
					
		
			}

		}
	
	}
}
void FamilyTree::runQueries(Query *queries, Person *answers, int queryCount)
{
	
//	int counter1= 1, counter2 = 1;
	//cout << hashTable.familyCount<<endl;
	for (int i =0; i < queryCount;i++) // change back to queryCount later
	{			
		PersonCopy *queue1 = new PersonCopy[100000];
		PersonCopy *queue2 = new PersonCopy[100000];
		int front1 = 0;
		int front2 = 0;
		int back1 = 1;
		int back2 = 1;
		int temp1 = 0;
		int temp2 = 0;
	//	PersonCopy person(queries[i].person1);
	//	PersonCopy person2(queries[i].person2);
		int indexPerson1 = hashTable.findPos(queries[i].person1);
		int indexPerson2 = hashTable.findPos(queries[i].person2);
	//	cout<<indexPerson1<<"person1 index"<<endl;
	//	cout<<indexPerson2<<"person2 index"<<endl;
		
		queue1[front1] = hashTable.array[indexPerson1].element;
		queue2[front2] = hashTable.array[indexPerson2].element;
		queue1[front1].indexValue = indexPerson1;
		queue2[front2].indexValue = indexPerson2;	
	//	cout<<queue1.currentSize<<endl;
	//	cout<<queue2.currentSize<<endl;
		
//		cout<<queue1[front1].indexParent<<endl;
//		cout<<queue2[front2].indexParent<<endl;
		while(front1 != back1)
		{
//			cout<< "inside queue1" <<endl;
			if (queue1[front1].indexParent == -1 && queue1[front1].indexParentSpouse == -1)
				break;
			if(queue1[front1].indexParent != -1)
			{
				temp1 = hashTable.findPos(hashTable.array[queue1[front1].indexParent].element);		
				queue1[back1] = hashTable.array[queue1[front1].indexParent].element;
				queue1[back1].indexValue = temp1;
			        
				 back1++;
			}
			if(queue1[front1].indexParentSpouse != -1)
			{
				temp1 = hashTable.findPos(hashTable.array[queue1[front1].indexParentSpouse].element);
				queue1[back1] = hashTable.array[queue1[front1].indexParentSpouse].element;
				queue1[back1].indexValue = temp1;
				back1++;
			}
			front1++;
//			cout<<back1++<<endl;	
		}	
		while(front2 != back2)
		{
		//	cout<< "inside queue2" <<endl;	
			if (queue2[front2].indexParent == -1 && queue2[front2].indexParentSpouse == -1)
				break;
			if(queue2[front2].indexParent != -1)
			{
				temp2 = hashTable.findPos(hashTable.array[queue2[front2].indexParent].element);
				queue2[back2] = hashTable.array[queue2[front2].indexParent].element;
				queue2[back2].indexValue = temp2;
				back2++;
			}
			if(queue2[front2].indexParentSpouse != -1)
			{
				temp2 = hashTable.findPos(hashTable.array[queue2[front2].indexParentSpouse].element);
				queue2[back2] = hashTable.array[queue2[front2].indexParentSpouse].element;
				queue2[back2].indexValue = temp2;
				back2++;
			}
			front2++;
	
		}
		back1++;
		back2++;
		qsort(queue1, back1, sizeof(PersonCopy), PersonCopy::compare);
		qsort(queue2, back2, sizeof(PersonCopy), PersonCopy::compare);					
		
		int j = 0;
		int k = 0;
		bool success = false;
		back1--;
		back2--;
		while(j != back1 || k != back2)
		{
		//	cout<<"person1"<<queue1[j].indexValue<<endl;
		//	cout<<"	"<<queue1[j].person.firstName<<endl;
		//	cout<<"/nperson2"<<queue2[k].indexValue<<endl;
		//	cout<<"	"<<queue2[k].person.firstName<<endl;
			if (queue1[j].indexValue == queue2[k].indexValue)
			{
				success = true;
				answers[i] = queue2[k].person;	
				break;
			}
			else if( queue1[j].person.year > queue2[k].person.year)
				j++;
			else if(queue1[j].person.year < queue2[k].person.year)
				k++;
			else 
			{
				int returnval = strcmp(queue1[j].person.firstName, queue2[k].person.firstName);
                			if (returnval < 0)
						j++;
					else
						k++;
			}
		}
	//	cout<<answers[i].firstName<<endl;
		if (success == false)
		{
			PersonCopy person;
			answers[i] = person.person;
		}
				
/*	 
		for(int j = 0; j < back1; j++)
		{
			cout<<queue1[j].person.firstName<<endl;
			cout<<queue1[j].indexValue<<endl;
			cout<<queue1[j].indexParent<<endl;		
			cout<<queue1[j].indexParentSpouse<<endl;
		}
		for(int k = 0; k < back2; k++)
		{
		cout<<queue2[k].person.firstName<<endl;
		cout<<queue2[k].indexValue<<endl;
		cout<<queue2[k].indexParent<<endl;
		cout<<queue2[k].indexParentSpouse<<endl;
	}*/

}  
}  // runQueries()

int PersonCopy::compare( const void *a, const void *b)
{
	if (((PersonCopy*)a)->person.year >((PersonCopy*)b)->person.year)
		return -1;
	if (((PersonCopy*)a)->person.year == ((PersonCopy*)b)->person.year)
	{
		int returnval = strcmp(((PersonCopy*)a)->person.firstName, ((PersonCopy*)b)->person.firstName);
		return returnval;
			
	}

	else
		return 1;   
}

PersonCopy::PersonCopy()
{

	person.year = -1;
	indexValue = 0;
	indexParent = -1;
	indexParentSpouse = -1;
}




//void FamilyTree
