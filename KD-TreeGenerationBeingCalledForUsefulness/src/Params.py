## Basic parameters
class Params(object):

    dataset = 'dataset/tiger_NMWA.dat' # input data
    resdir = 'output/' # output dir

    NDATA = None
    NDIM = None
    LOW = None
    HIGH = None
    SRT = 0.01 # sampling rate
    nQuery = 200 # number of queries
    maxHeight = 10 # maximum tree height
    unitGrid = 0.01 # cell unit in kd-cell
    
    def __init__(self, seed):
        self.Eps = 0.5  # epsilon
        self.k = 50
        self.Seed = seed
        self.minPartSize = 2 ** 5  # maximum number of data points in a leaf node
        self.Percent = 0.3 # budget allocated for split
        self.switchLevel = 5 # switch level for hybrid tree
        self.Res = 18 # Hilbert-R tree resolution
        self.useLeafOnly = False # only True for kd-cell and kd-noisymean        
        self.cellDistance = 40 # threshold to test uniformity in kd-cell

#        self.geoBudget = 'none' # uniform budgeting
#        self.geoBudget = 'aggressive' # geometric exponent i
#        self.geoBudget = 'quadratic' # geometric exponent i/2
        self.geoBudget = 'optimal' # geometric exponent i/3
#        self.geoBudget = 'quartic' # geometric exponent i/4
       
        self.splitScheme = 'expo' # exponential mechanism
#        self.splitScheme = 'noisyMean' # noisy mean approximation







    
    
    














#SAMPLE = True # switch for sampling
