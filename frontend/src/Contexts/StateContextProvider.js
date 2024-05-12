import React, { createContext, useContext, useState } from 'react';

const StateContext = createContext();
const baseUrl = ' http://localhost:8080/api/queryquest/';  // this should be backend api 

export const StateContextProvider = ({ children }) => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchTime, setSearchTime] = useState(0);

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

  return (
    <StateContext.Provider value={{ getResults, results, searchTerm, setSearchTerm, loading,searchTime, setSearchTime }}>
      {children}
    </StateContext.Provider>
  );
};

export const useStateContext = () => useContext(StateContext);