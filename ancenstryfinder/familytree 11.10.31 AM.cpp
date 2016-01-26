#include "familytree.h"
#include <string.h>
#include <cstdlib>
#include <iostream>
#include <cstring>


FamilyTree::FamilyTree(Family *families, int familyCount):table(2* familyCount)
{
	int parent1;
	int parent2;
	for (int i = 0; i < familyCount; i++)
	{
		parent1 = table.insert(families[i].person);	
		if (families[i].spouse.year != -1)
			parent2 = table.insert(families[i].spouse);

		else		
			parent2 = -1;
		for(int j = 0; j < families[i].childCount; j++)
		{	
				int indexChild = table.insert(families[i].children[j]);
				table.array[indexChild].parent1 = parent1;
				table.array[indexChild].parent2 = parent2;
		}
	
	}
}

int compare(const void *a, const void *b)
{
        int diff = ((YearIndex*)b)->year - ((YearIndex*)a)->year;
        if(diff !=0)
        {
                return diff;
        }
        return ((YearIndex*)b)->index - ((YearIndex*)a)->index;
}

void FamilyTree::runQueries(Query *queries, Person *answers, int queryCount)
{
 	YearIndex *ancestor = new YearIndex[100000];
	YearIndex *ancestor2 = new YearIndex[100000];
	int front, back, front2, back2, j, j2;	
	for (int i = 0; i < queryCount;i++) // change back to queryCount later
	{			
		back = 0, front = 0, back2 = 0, front2 = 0;
		ancestor[back].index = table.insert(queries[i].person1);
		ancestor[back].year = table.array[ancestor[back].index].person.year;
		back++;
		

		while(back != front)
		{
			if(table.array[ancestor[front].index].parent1 >= 0)
			{
				ancestor[back].index = table.array[ancestor[front].index].parent1;
				ancestor[back].year = table.array[ancestor[back].index].person.year;
				back++;
			}

			if(table.array[ancestor[front].index].parent2 >= 0)
                        {
                                ancestor[back].index = table.array[ancestor[front].index].parent2;
                                ancestor[back].year = table.array[ancestor[back].index].person.year;
                                back++;
                        }
			front++;
		}
		ancestor2[back2].index = table.insert(queries[i].person2);
		ancestor2[back2].year = table.array[ancestor2[back2].index].person.year;
		back2++; 

		 while(back2 != front2)
                {
                        if(table.array[ancestor2[front2].index].parent1 >= 0)
                        {
                                ancestor2[back2].index = table.array[ancestor2[front2].index].parent1;
                                ancestor2[back2].year = table.array[ancestor2[back2].index].person.year;
                                back2++;
                        }

                        if(table.array[ancestor2[front2].index].parent2 >= 0)
                        {
                                ancestor2[back2].index = table.array[ancestor2[front2].index].parent2;
                                ancestor2[back2].year = table.array[ancestor2[back2].index].person.year;
                                back2++;
                        }
                        front2++;
                }

		qsort(ancestor, back, sizeof(YearIndex), compare);
		qsort(ancestor2, back2, sizeof(YearIndex), compare);
		j = 0;
		j2 = 0;
		while(j < back && j2 < back2)
			if(ancestor[j].year > ancestor2[j2].year)
			{
				j++;

			}

			else
				if(ancestor[j].year < ancestor2[j2].year)
					j2++;
				else 
				   if(ancestor[j].index >  ancestor2[j2].index)
					j++;
				else
				   if(ancestor[j].index < ancestor2[j2].index)
					j2++;
				   else{
					memcpy(&answers[i], &table.array[ancestor[j].index].person, sizeof(Person));
					break;
				}
			if(j == back || j2 == back2)
			   answers[i].year = -1;
		}

	
		delete[] ancestor;
		delete[] ancestor2;
}  // runQueries()

