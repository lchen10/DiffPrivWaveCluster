from collections import defaultdict
import copy
import logging
import math
import sys
import os
import random
from scipy import stats


import numpy



def intersect(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2):
    if ((xmin1 >= xmin2 and xmin1 < xmax2) or (xmax1 >= xmin2 and xmax1 < xmax2)) and ((ymin1 >= ymin2 and ymin1 < ymax2) or (ymax1 >= ymin1 and ymax1 < ymax2)):
        return True
    return False

def contains_point(xmin, xmax, ymin, ymax, x, y):
    if (x > xmin-0.00001 and x < xmax+0.000001 and y > ymin-0.00001 and y < ymax+0.00001):
        return True
    else:
        return False
        
def fraction(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2):
    i_xmin = max(xmin1, xmin2)
    i_xmax = min(xmax1, xmax2)
    i_ymin = max(ymin1, ymin2)
    i_ymax = min(ymax1, ymax2)
    if (i_xmin >= i_xmax or i_ymin >= i_ymax):
        return 0
    elif (i_xmin <= xmin2 + 0.000001 and i_xmax >= xmax2 - 0.000001 and i_ymin <= ymin2 + 0.000001 and i_ymax >= ymax2 - 0.000001):
        return 1
    else:
        return (i_xmax - i_xmin) * (i_ymax - i_ymin) / ((xmax2 - xmin2) * (ymax2 - ymin2))
        
class Node(object):
    def __init__(self, xmin, xmax, ymin, ymax, alpha = 1.0):
        self.xmin = xmin
        self.xmax = xmax
        self.ymin = ymin
        self.ymax = ymax
        self.count = 0
        self.points = []
        self.N = 1
        self.nodes = {}
        self.alpha = alpha
        
        
    
        

    def add_point(self, x, y):
        if (x < self.xmin - 0.000001 or x > self.xmax + 0.000001 or y < self.ymin-0.000001 or y > self.ymax+0.000001):
            raise Exception("Point out of range for Node")
        self.points.append((x,y))
        self.count += 1

    def add_noise(self, eps):
        top_eps = self.alpha * eps
        noise = numpy.random.laplace(size=1, scale=1.0/top_eps)[0]
        self.noisy_count = self.count + noise
        

    def partition(self, eps, partition_par):
        
        bottom_eps = (1 - self.alpha) * eps

        if (self.noisy_count < partition_par * 4):
            new_count = self.count + numpy.random.laplace(size=1, scale=1.0/bottom_eps)[0]
            a = (1.0/self.alpha)**2
            b = (1.0/(1- self.alpha))**2
            self.noisy_count = (b * self.noisy_count + a * new_count) / (b+a)
#            print "###first-level 0-count node: xmin=%d, ymin=%d, xmax=%d, ymax=%d, count=%d, noisy_count = %d, N=%d \n" % (self.xmin, self.ymin, self.xmax, self.ymax, self.count, self.noisy_count, self.N)
#            print "###0-count further partition number, self.N=%d \n" % (self.N)
            print "%d,%d,%d,%d,%d,%d,%d" % (self.xmin, self.ymin, self.xmax, self.ymax, self.count, self.noisy_count, self.N)
            return (0, 0)
        
        self.N = int(math.ceil(math.sqrt(self.noisy_count * bottom_eps / partition_par)))
#        print "###first-level node: xmin=%d, ymin=%d, xmax=%d, ymax=%d, count=%d, noisy_count = %d, N=%d \n" % (self.xmin, self.ymin, self.xmax, self.ymax, self.count, self.noisy_count, self.N)
#        print "###its further partition number, self.N=%d \n" % (self.N)
        print "%d,%d,%d,%d,%d,%d,%d" % (self.xmin, self.ymin, self.xmax, self.ymax, self.count, self.noisy_count, self.N)
        self.xstep = (float(self.xmax) - self.xmin)/self.N
        self.ystep = (float(self.ymax) - self.ymin)/self.N
         
        
        # i values from 0 to n-1. 
        for i in xrange(self.N):
            self.nodes[i] = {}
            x = self.xmin + i * self.xstep
            for j in xrange(self.N):
                y = self.ymin + j * self.ystep
                self.nodes[i][j] = Node(x, x+self.xstep, y, y+self.ystep, self.alpha)

        for (x,y) in self.points:
            x_index = int((x - self.xmin) / self.xstep)
            y_index = int((y - self.ymin) / self.ystep)
            self.nodes[x_index][y_index].add_point(x,y)

        noisy_sum = 0.0
        for i in xrange(self.N):
            for j in xrange(self.N):
                self.nodes[i][j].noise = numpy.random.laplace(size=1, scale=1.0/bottom_eps)[0]
                self.nodes[i][j].noisy_count = self.nodes[i][j].count + self.nodes[i][j].noise
                noisy_sum += self.nodes[i][j].noisy_count
        
        a = (1.0/self.alpha)**2
        b = ((1.0/(1-self.alpha))*self.N)**2
        self.noisy_count = (self.noisy_count * b + noisy_sum * a) / (a+b)

        diff = (self.noisy_count - noisy_sum) / (self.N*self.N)
        for i in xrange(self.N):
            for j in xrange(self.N):
                self.nodes[i][j].noisy_count += diff
        return (1, self.N ** 2)

    def adjust(self):
        if (self.N == 1):
            return
        noisy_sum = 0.0
        for i in xrange(self.N):
            for j in xrange(self.N):
                noisy_sum += self.nodes[i][j].noisy_count
        diff = (self.noisy_count - noisy_sum) / (self.N*self.N)
        for i in xrange(self.N):
            for j in xrange(self.N):
                self.nodes[i][j].noisy_count += diff
        
    
    def accurate_answer(self, xmin, xmax, ymin, ymax):
        a = 0
        for (x,y) in self.points:
            if contains_point(xmin, xmax, ymin, ymax, x, y):
                a += 1
        return a

    def answer_query(self, xmin, xmax, ymin, ymax):
        f = fraction(xmin, xmax, ymin, ymax, self.xmin, self.xmax, self.ymin, self.ymax)
        #print (f, xmin, xmax, ymin, ymax, self.xmin, self.xmax, self.ymin, self.ymax)
        assert f > -0.000001 and f < 1.000001
        if f == 0:
            return (0, 0, 0, 0, 0, 0, 0, 0)
        elif f == 1:
            return (1, self.count, self.noisy_count, self.count, 1, 0, 0, 0)
        else:
            true_answer = self.accurate_answer(xmin, xmax, ymin, ymax)
            if (self.N == 1):
                return (2, self.count*f, self.noisy_count*f, true_answer, 0, 1, 0, 0)
            else:
                whole = 0
                part = 0
                fsum = 0
                nsum = 0
                asum = 0
                for i in xrange(self.N):
                    for j in xrange(self.N):
                        (ty,fc,nc,ac,wh1,pa1,wh2,pa2) = self.nodes[i][j].answer_query(xmin, xmax, ymin, ymax)
                        fsum += fc
                        nsum += nc
                        asum += ac
                        whole += wh1 + wh2
                        part += pa1 + pa2
                return (2, fsum, nsum, asum, 0, 1, whole, part)
                

            
        
class Flat_origin(object):
    def node_count(self):
        count = self.N ** 2
        for i in xrange(self.N):
            for j in xrange(self.N):
                count += self.nodes[i][j].N ** 2
        return count
        
    def construct_from_ndarray(self, data, num_partitions=11, alpha = 1.0):
        """
            data is a numpy ndarray
        """
   
        self.N = num_partitions
   
        #logging.debug('constructing data grid...')
        dimensions = data.shape[0]
        self.total_num = data.shape[1]
        #print self.total_num
        
        self.xmin = numpy.min(data[0])-0.000001
        self.xmax = numpy.max(data[0])+0.000001
        self.ymin = numpy.min(data[1])-0.000001
        self.ymax = numpy.max(data[1])+0.000001
        
        self.xstep = (float(self.xmax) - self.xmin)/num_partitions
        self.ystep = (float(self.ymax) - self.ymin)/num_partitions
        
        self.nodes = {}
        
        #self.x_values = [x for x in sorted(frange(self.xmin, self.xmax, self.xstep))]
        #self.y_values = [y for y in sorted(frange(self.ymin, self.ymax, self.ystep))]
        
        self.data = data
        for i in xrange(self.N):
            self.nodes[i] = {}
            x = self.xmin + i * self.xstep
            for j in xrange(self.N):
                y = self.ymin + j * self.ystep
                self.nodes[i][j] = Node(x, x+self.xstep, y, y+self.ystep, alpha)
        
        #print xmin, xmax, self.xstep, self.x_values
        #print ymin, ymax, self.ystep, self.y_values

        #self.counts = numpy.ndarray(shape = [self.N,self.N], buffer = numpy.array([0.0,]*(self.N*self.N)))
        for i in xrange(self.data.shape[1]):
            x_index = int((self.data[0,i] - self.xmin) / self.xstep)
            y_index = int((self.data[1,i] - self.ymin) / self.ystep)
            self.nodes[x_index][y_index].add_point(self.data[0,i], self.data[1,i])
        
    def __init__(self):
        self.N = None
        self.partition_num = 0
        self.node_num = 0
    
    # set non_neg = True, all the negative will be equal to 0
    def anonymize(self, eps = 1.0, is_partition = False, non_neg = False, partition_par = 20.0):
        for i in xrange(self.N):
            for j in xrange(self.N):
                self.nodes[i][j].add_noise(eps)
        self.node_num = self.N ** 2
        if is_partition:
            for i in xrange(self.N):
                for j in xrange(self.N):
                    (pn, nn) = self.nodes[i][j].partition(eps, partition_par)
                    self.partition_num += pn
                    self.node_num += nn
        
        # make sure that all nodes are non-negative
        if non_neg:
            for i in xrange(self.N):
                for j in xrange(self.N):
                    if self.nodes[i][j].noisy_count < 0:
                        self.nodes[i][j].noisy_count = 0
        
    def print_data(self):
        for i in xrange(self.N):
            for j in xrange(self.N):
                if (self.nodes[i][j].noisy_count < 0 or self.nodes[i][j].count == 0):
                    print "i=%d, j=%d, count=%d, noisy_count=%f" % (i, j, self.count, self.noisy_count)
                
    def _answer_query(self, xmin, xmax, ymin, ymax, data=None):
        added_noise = 0
        sol_actual = 0

        whole1 = 0
        part1 = 0
        whole2 = 0
        part2 = 0
        fsum = 0
        nsum = 0
        asum = 0
        for i in xrange(self.N):
            for j in xrange(self.N):
                (ty,fc,nc,ac,wh1,pa1,wh2,pa2) = self.nodes[i][j].answer_query(xmin, xmax, ymin, ymax)
                #if (abs(fc-ac) > 10):
                #    print "i=%d, j=%d, count=%d, error=%f" % (i, j, self.nodes[i][j].count, fc-ac)
                fsum += fc
                nsum += nc
                asum += ac
                whole1 += wh1
                part1 += pa1
                whole2 += wh2
                part2 += pa2
        #print nsum, asum
        d = max(asum, self.total_num * 0.001)
        rel = abs(nsum - asum) * 1.0 / d
        return fsum, nsum, asum, rel, abs(fsum-asum), abs(nsum-asum), whole1, part1, whole2, part2
    
    def answer_query_set(self, queries):
        #self.print_data()
        
        f_abs_errors = []
        f_rel_errors = []
        n_abs_errors = []
        n_rel_errors = []
        n_whole1 = []
        n_part1 = []
        n_whole2 = []
        n_part2 = []
        for i in xrange(len(queries)):
            ((xmin, ymin), (xmax, ymax)) = queries[i]
            (fsum, nsum, asum, rel, f_abs, n_abs, whole1, part1, whole2, part2) = self._answer_query(xmin, xmax, ymin, ymax) 

            
            f_abs_errors.append(f_abs)
            f_rel_errors.append(rel)
            n_abs_errors.append(n_abs)
            n_rel_errors.append(float(n_abs)/asum if asum != 0 else float(f_abs))
            n_whole1.append(whole1)
            n_part1.append(part1)
            n_whole2.append(whole2)
            n_part2.append(part2)
            
        
        
        print stats.scoreatpercentile(n_abs_errors, 25), stats.scoreatpercentile(n_abs_errors, 50), numpy.mean(n_abs_errors), stats.scoreatpercentile(n_abs_errors, 75), stats.scoreatpercentile(n_abs_errors, 95)
        
        return stats.scoreatpercentile(f_rel_errors, 25), stats.scoreatpercentile(f_rel_errors, 50), numpy.mean(f_rel_errors), stats.scoreatpercentile(f_rel_errors, 75), stats.scoreatpercentile(f_rel_errors, 95), stats.scoreatpercentile(n_abs_errors, 25), stats.scoreatpercentile(n_abs_errors, 50), numpy.mean(n_abs_errors), stats.scoreatpercentile(n_abs_errors, 75), stats.scoreatpercentile(n_abs_errors, 95)


def norm2(list):
    list1 = copy.deepcopy(list)
    for i in xrange(len(list1)):
        list1[i] = list1[i] ** 2
    return math.sqrt(numpy.mean(list1))
        
def main(inputfile, inputeps, outputfile):
    print inputfile
    print inputeps
    print outputfile
#    data = numpy.genfromtxt('spiral.dat', unpack=True)
    data = numpy.genfromtxt(inputfile, unpack=True)
    hist = Flat_origin()
#    from the paper description, we can see that total count N cost a little budget, derivating the grid size does not cost any privacy budget.
#    since it is directly computed over sqrt(N'*epsilon / 10), all these values are public. epsilon will be fully used in reporting noisy counts 
#    for each grid. For adaptive grid, we can see that alpha * epsilon used for reporting first level noisy count, (1-alpha) * epsilon is used for 
#    reporting second level noisy count. m1, m2, (grid size for the two levels) does not cost any budget. 
    
    
    
#     code explanation: if it is uniform grid partition, you specified the grid number in hist.construct_from_ndarray(data, num_partitions=12, alpha = 1.0)
#    this code is used to divided into several partitions, then generate noisy counts for each partition,which also include the rectangle boundary for each partition
#    if it is adaptive grid partition, then you need to set first level grid size = 10 in hist.construct_from_ndarray(data, num_partitions=10, alpha = 1.0)
#     then hist.anonymize(eps=1.0, is_partition = True, non_neg = True, partition_par = 5) will further divide into second level and generate noisy counts, set is_partition = True.
    
    deps = float(inputeps)
    hist.construct_from_ndarray(data, num_partitions=10, alpha = 0.5) #the first level partition will be np*np, when is UG, alpha = 1.0 (alpha is the proportion of budget for the first level), when is AG, alpha = 0.5
    hist.anonymize(eps=deps, is_partition = True, non_neg = True, partition_par = 5) #pp is c_2 in the paper, which is 5   
#    query = [[578509, 578509], [602013, 602013]]
#    queries = [query]
#    ans = hist.answer_query_set(queries)

    counter = 0;
#    f=open("E:/D_disk/workspace/AdaptiveGridGeneration/data_grids/spiral-grids-10.txt", "w")
    f=open(outputfile, "w")
    
    
    for i in xrange(hist.N):
        for j in xrange(hist.N):
            node = hist.nodes[i][j]
            
            if node.N == 1:
#                print "xmin=%d, ymin=%d, xmax=%d, ymax=%d, count=%d, noisy_count = %d, N=%d \n" % (node.xmin, node.ymin, node.xmax, node.ymax, node.count, node.noisy_count, node.N)
#                print "%d,%d,%d,%d,%d,%d" % (node.count, node.noisy_count, node.xmin, node.ymin, node.xmax, node.ymax)
#                f.write("%d,%d,%d,%d,%d,%d,%d \n" % (node.count, node.noisy_count, node.N, node.xmin, node.ymin, node.xmax, node.ymax))
                f.write("%d,%d,%d,%d,%d \n" % (node.noisy_count, node.xmin, node.ymin, node.xmax, node.ymax))
#                f.write(`node.count` + ', ' + `node.noisy_count` + ', ' + `node.xmin`  + ', ' + `node.ymin`  + ', ' + `node.xmax`  + ', ' + `node.ymax`)
                counter = counter +1
            if node.N > 1:
                for m in xrange(node.N):
                    for n in xrange(node.N):
                        subnode = node.nodes[m][n]
                        counter = counter +1
#                        print "###subnode: xmin=%d, ymin=%d, xmax=%d, ymax=%d, count=%d, noisy_count = %d, N=%d \n" % (subnode.xmin, subnode.ymin, subnode.xmax, subnode.ymax, subnode.count, subnode.noisy_count, subnode.N)
#                        print "%d,%d,%d,%d,%d,%d" % (subnode.count, subnode.noisy_count, subnode.xmin, subnode.ymin, subnode.xmax, subnode.ymax)
#                        f.write("%d,%d,%d,%d,%d,%d,%d \n" % (subnode.count, subnode.noisy_count, node.N, subnode.xmin, subnode.ymin, subnode.xmax, subnode.ymax))
                        f.write("%d,%d,%d,%d,%d \n" % (subnode.noisy_count, subnode.xmin, subnode.ymin, subnode.xmax, subnode.ymax))
    print "%d" % (counter)        
    logging.basicConfig(level=logging.DEBUG, stream=sys.stdout)
    f.close()
    

if __name__ == '__main__':
    print(sys.argv)
    main(sys.argv[1],sys.argv[2],sys.argv[3])
