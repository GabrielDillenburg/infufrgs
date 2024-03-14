# ERAD/RS 2014 Minicourse Text
## Performance Analysis of Parallel Programs

### Course Plan as of 2024-03-08

#### Semester 1, 2024
- **CMP270** - Introduction to High Performance Computing
- **CMP301** - Research Project

#### Semester 2, 2024
- **CMP223** - Computer System Performance Analysis
  - Instructor: Prof. Luciano Gaspary
  - Experimental project inspired by Jain 1991
    - "The Art of Computer Systems Performance Analysis: Techniques for Experimental Design, Measurement, Simulation, and Modeling" [Wiley Link](https://www.wiley.com/en-br/The+Art+of+Computer+Systems+Performance+Analysis%3A+Techniques+for+Experimental+Design%2C+Measurement%2C+Simulation%2C+and+Modeling-p-9780471503361)
- **CMP302** - Research Project

### CMP301 Research Project Initial Idea (2024-03-08)

#### Option 2
Exploring the tracking of parallel applications using the OMPT interface as done in this minicourse. Focus on the tracker and its ability to generate data for visualizing program behavior. The tracker's code is available at [OMPT Tracker Code](https://gitlab.com/lnesi/companion-minicurso-openmp-tasks/-/tree/master/codigos/OMPT).

#### General Discussion: OpenMP

##### Example OpenMP Program

```c
#include <stdio.h>
#include <omp.h>

int main(int argc, char** argv){
  // Sequential part
  #pragma omp parallel
  {
    // Parallel part
    printf("Hello from thread: %d\n", omp_get_thread_num());
  }
  // Back to sequential
  return 0;
}
