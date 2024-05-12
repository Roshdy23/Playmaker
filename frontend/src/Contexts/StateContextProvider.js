import React, { createContext, useContext, useState } from 'react';

const StateContext = createContext();
const baseUrl = 'http://localhost:8080/api/queryquest/';  // this should be backend api 

export const StateContextProvider = ({ children }) => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchTime, setSearchTime] = useState(0);
  const [searchTerm2, setSearchTerm2] = useState('');

  const [Sugs, setSugs] = useState([]);

  const getResults = async (url) => {
    const currentTime = performance.now();
    setLoading(true);
 
    console.log(url);

    // const res = await fetch(`${baseUrl}${url}`, {
    //   method: 'GET',
    
    // }); 
    fetch(`${baseUrl}${url}`).then(res =>res.json).then(data =>setResults(data));
    

    

  
    // const data = await res.json();
    // console.log(data);
    // setResults(data);
    setSearchTime(performance.now()- currentTime);
    setLoading(false);
  };

  
  const getSugs= async (url) => {
   
  
 
    console.log(url);

    // const res = await fetch(`${baseUrl}${url}`, {
    //   method: 'GET',
    
    // }); 
    // fetch(`${baseUrl}${url}`).then(res =>res.json).then(data =>setResults(data));
    
    fetch(`http://localhost:8080/api/queryquest/prvQueries/${url}`).then(res =>res.json).then(data => Object.keys(data).length<=10?setSugs(data):setSugs(data.slice(0,9)));

  
    // const data = await res.json();
    // console.log(data);
    // setResults(data);


  };

  return (
    <StateContext.Provider value={{ getResults, results, searchTerm, setSearchTerm, loading,searchTime, setSearchTime,Sugs,getSugs,searchTerm2,setSearchTerm2,setSugs }}>
      {children}
    </StateContext.Provider>
  );
};

export const useStateContext = () => useContext(StateContext);