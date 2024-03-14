# Performance Analysis of Parallel Programs

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
```
##### Compiling and Running the Example

```shell
gcc example_openmp.c -o programa -fopenmp
ls -larth programa
ldd programa
env OMP_TOOL_LIBRARIES=$(PWD)/libompt.so ./programa
lscpu
```
##### Results
```bash
-rwxr-xr-x 1 schnorr schnorr 16K Mar  8 14:00 programa
linux-vdso.so.1 (0x00007ffe261e6000)
/lib/x86_64-linux-gnu/libgomp.so.1 (0x00007ff44aeae000)
/lib/x86_64-linux-gnu/libc.so.6 (0x00007ff44accc000)
/lib64/ld-linux-x86-64.so.2 (0x00007ff44af40000)

Hello from thread: 1
Hello from thread: 3
Hello from thread: 2
Hello from thread: 0
```

## Objective
Visualize tasks over time across a set of computer programs written using OpenMP Tasks.

## What is Task Parallelism?

[OpenMP Tasks Introduction Slides](https://gitlab.com/lnesi/companion-minicurso-openmp-tasks/-/blob/master/slide.pdf?ref_type=heads)

## Employing OMPT with a Focus on Tracking OpenMP Tasks (Task Parallelism)

### OMPT
The OMPT interface is designed for performance monitoring of OpenMP applications. It provides a standardized way to inspect the state and behavior of OpenMP programs, which is crucial for understanding and optimizing task parallelism.

[OMPT Specification](https://www.openmp.org/spec-html/5.0/openmpch4.html)

### Recommended Reading

- "Make the Most of OpenMP Tasking" by Sergi Mateo Bellido, a compiler engineer. This presentation from SC17 provides insights into effectively utilizing OpenMP's tasking model for parallel programming.

  [Make the Most of OpenMP Tasking (PDF)](https://www.openmp.org/wp-content/uploads/SC17-Bellido-MakeTheMostOfOpenMPTasking.pdf)



## Applications/Benchmarks to Use

### Kastors Benchmark
The Kastors benchmark suite is designed to evaluate OpenMP's task dependencies feature.

- [Kastors Benchmark Suite](https://gitlab.inria.fr/openmp/kastors)

#### Reference
```bibtex
@inproceedings{virouleau2014evaluation,
  title={Evaluation of OpenMP dependent tasks with the KASTORS benchmark suite},
  author={Virouleau, Philippe and Brunet, Pierrick and Broquedis, Fran{\c{c}}ois and Furmento, Nathalie and Thibault, Samuel and Aumage, Olivier and Gautier, Thierry},
  booktitle={Using and Improving OpenMP for Devices, Tasks, and More: 10th International Workshop on OpenMP, IWOMP 2014, Salvador, Brazil, September 28-30, 2014. Proceedings 10},
  pages={16--29},
  year={2014},
  organization={Springer}
}
```

# Applications to Track

- **jacobi**: Solving linear systems using the Jacobi method. [Jacobi Method Wikipedia](https://en.wikipedia.org/wiki/Jacobi_method)
- **plasma**: Further research required.
- **sparselu**: LU factorization for sparse matrices.
- **strassen**: Strassen's matrix multiplication method, utilizing matrix transpose.

# Getting Started

1. Clone the repository.
2. Read the README.
3. Compile the benchmarks.
4. Locate the application binaries.
5. Execute each application with our tracking library.

**Note**:
- You need to define the workload, which should be specified in the README files.
- Obtain our `libompt.so` file from [OMPT Code Repository](https://gitlab.com/lnesi/companion-minicurso-openmp-tasks/-/tree/master/codigos/OMPT).

Redirect the standard output to a file for analysis.

```shell
env OMP_TOOL_LIBRARIES=$(PWD)/libompt.so ./programa (INPUT) > trace.txt
```

# Visualizing the Trace

To visualize the output, employ an R script designed for parsing and graphically representing the trace data.

# Additional Benchmarks

Explore these benchmarks for further insights into OpenMP's capabilities and performance:

- **OMP-TDB**: A tool for debugging and performance analysis of OpenMP applications. [GitHub Repository](https://github.com/devreal/omp-tdb)
- **EPCC OpenMP v4.0 "taskbench"**: A benchmark suite for evaluating OpenMP tasking performance. [GitHub Repository](https://github.com/EPCCed/epcc-openmp-microbenchmarks)
- **HYDRO HydroC/HydroCplusMPI**: A hydrodynamics benchmark suite that supports MPI. [GitHub Repository](https://github.com/HydroBench/Hydro)
- **NOT** Dense Matrix QR Factorization: Suggested by Marcelo Miletto, this benchmark focuses on the QR factorization of dense matrices, highlighting computational challenges and performance opportunities.

# Example #3 - Matrix Multiplication

This example showcases block-wise matrix multiplication, a technique that can significantly improve performance on modern hardware by better utilizing cache memory.

Matrices are square (NxN elements), and multiplication is performed on square blocks (BSxBS elements), optimizing memory access patterns and computational efficiency.

This example illustrates block-wise matrix multiplication, a technique that optimizes computational efficiency and memory access patterns by operating on square blocks of a matrix. The matrices involved are square, with NxN elements, and the multiplication is performed on square blocks of BSxBS elements.

The following OpenMP code snippet demonstrates how to apply parallel processing to this matrix multiplication, utilizing task dependencies to manage the computation flow and ensure correct execution order.

```c
#pragma omp parallel
#pragma omp single
{
  for(k=0; k < min(b,mb); k++) { // for each diagonal block
    kb = k*nb*n + k*nb;
    #pragma omp task depend(inout: A[kb:kb], A[kb+1:kb+1]) depend(out: T[0:tsize])
    LAPACKE_dgeqrt(LAPACK_ROW_MAJOR, nb, nb, nb, &A[kb], lda, T, ldt);

    for(j=k+1; j<b; j++) { // update diagonal right blocks 
      jb = k*nb*n + j*nb;
      #pragma omp task depend(in: A[kb:kb], T[0:tsize]) depend(inout: A[jb:jb+nb])
      LAPACKE_dlarfb(LAPACK_ROW_MAJOR, 'L', 'T', 'F', 'C', nb, nb, nb, &A[kb], lda, T, ldt, &A[jb], lda);
    }
          
    for(i=k+1; i<mb; i++) { // eliminate blocks below the diagonal
      ib = i*nb*n + k*nb;
      #pragma omp task depend(inout: A[kb+1:kb+1], A[ib:ib+nb]) depend(out: T2[0:tsize])
      LAPACKE_dtpqrt(LAPACK_ROW_MAJOR, nb, nb, 0, nb, &A[kb], lda, &A[ib], lda, T2, ldt);

      for(zit=k+1, z=1; zit<b; zit++, z++) { // update k-th line with i-th line
        jb = k*nb*n + zit*nb;
        ib2 = (i*nb*n + k*nb) + z*nb;
        #pragma omp task depend(inout: A[jb:jb+nb], A[ib2:ib2+nb]) depend(in: A[ib:ib+nb], T2[0:tsize])
        LAPACKE_dtpmqrt(LAPACK_ROW_MAJOR, 'L', 'T', nb, nb, nb, 0, nb, &A[ib], lda, T2, ldt, &A[jb], lda, &A[ib2], lda);
      }
    }
  }
}
```