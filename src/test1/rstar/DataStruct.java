package test1.rstar;
class DataStruct
{
    int dimension;
    float[] Data;
    
    DataStruct(int _dimension)
    {
        dimension = _dimension;
        Data = new float[dimension];
    }
}