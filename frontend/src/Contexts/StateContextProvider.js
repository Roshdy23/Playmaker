import React, { createContext, useContext, useState } from 'react';

const StateContext = createContext();
const baseUrl = 'https://google-web-search1.p.rapidapi.com/';  // this should be backend api 

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
    //   headers: {
    //     'X-RapidAPI-Key': '8552045084msh735644ab1c73cf4p1d278fjsnaf8d906ddec3',
    //     'X-RapidAPI-Host': 'google-web-search1.p.rapidapi.com'
    //   },
    // }); 
    

    const temp = [
      {
          title: 'Example Website 1',
          url: 'https://www.example.com/1',
          description: 'This is an example snippet containing the query words.',
      },
      {
          title: 'Example Website 2',
          url: 'https://www.example.com/2',
          description: 'Another example snippet with the query words highlighted.',
      },
      {
        title: 'Example Website 1',
        url: 'https://www.example.com/1',
        description: 'This    is an example snippet containing the query words.',
    },
    {
        title: 'Example Website 2',
        url: 'https://www.example.com/2',
        description: 'Another example snippet with the query words highlighted.',
    },
    {
      title: 'Example Website 1',
      url: 'https://www.example.com/1',
      description: 'This is an example snippet containing the query words.',
  },
  {
      title: 'Example Website 2',
      url: 'https://www.example.com/2',
      description: 'Another example snippet with the query words highlighted.',
  },
  {
    title: 'Example Website 1',
    url: 'https://www.example.com/1',
    description: 'This    is an example snippet containing the query words.',
},
{
    title: 'Example Website 2',
    url: 'https://www.example.com/2',
    description: 'Another example snippet with the query words highlighted.',
},
{
  title: 'Example Website 1',
  url: 'https://www.example.com/1',
  description: 'This is an example snippet containing the query words.',
},
{
  title: 'Example Website 2',
  url: 'https://www.example.com/2',
  description: 'Another example snippet with the query words highlighted.',
},
{
title: 'Example Website 1',
url: 'https://www.example.com/1',
description: 'This    is an example snippet containing the query words.',
},
{
title: 'Example Website 2',
url: 'https://www.example.com/2',
description: 'Another example snippet with the query words highlighted.',
},
{
  title: 'Example Website 1',
  url: 'https://www.example.com/1',
  description: 'This is an example snippet containing the query words.',
},
{
  title: 'Example Website 2',
  url: 'https://www.example.com/2',
  description: 'Another example snippet with the query words highlighted.',
},
{
title: 'Example Website 1',
url: 'https://www.example.com/1',
description: 'This    is an example snippet containing the query words.',
},
{
title: 'Example Website 2',
url: 'https://www.example.com/2',
description: 'Another example snippet with the query words highlighted.',
},
{
  title: 'Example Website 1',
  url: 'https://www.example.com/1',
  description: 'This is an example snippet containing the query words.',
},
{
  title: 'Example Website 2',
  url: 'https://www.example.com/2',
  description: 'Another example snippet with the query words highlighted.',
},
{
title: 'Example Website 1',
url: 'https://www.example.com/1',
description: 'This    is an example snippet containing the query words.',
},
{
title: 'Example Website 2',
url: 'https://www.example.com/2',
description: 'Another example snippet with the query words highlighted.',
},
{
  title: 'Example Website 1',
  url: 'https://www.example.com/1',
  description: 'This is an example snippet containing the query words.',
},
{
  title: 'Example Website 2',
  url: 'https://www.example.com/2',
  description: 'Another example snippet with the query words highlighted.',
},
{
title: 'Example Website 1',
url: 'https://www.example.com/1',
description: 'This    is an example snippet containing the query words.',
},
{
title: 'Example Website 2',
url: 'https://www.example.com/2',
description: 'Another example snippet with the query words highlighted.',
},
{
  title: 'Example Website 1',
  url: 'https://www.example.com/1',
  description: 'This is an example snippet containing the query words.',
},
{
  title: 'Example Website 2',
  url: 'https://www.example.com/2',
  description: 'Another example snippet with the query words highlighted.',
},
{
title: 'Example Website 1',
url: 'https://www.example.com/1',
description: 'This    is an example snippet containing the query words.',
},
{
title: 'Example Website 2',
url: 'https://www.example.com/2',
description: 'Another example snippet with the query words highlighted.',
},
{
  title: 'Example Website 1',
  url: 'https://www.example.com/1',
  description: 'This is an example snippet containing the query words.',
},
{
  title: 'Example Website 2',
  url: 'https://www.example.com/2',
  description: 'Another example snippet with the query words highlighted.',
},
{
title: 'Example Website 1',
url: 'https://www.example.com/1',
description: 'This    is an example snippet containing the query words.',
},
{
title: 'Example Website 2',
url: 'https://www.example.com/2',
description: 'Another example snippet with the query words highlighted.',
},
  ];

  
    // const data = await res.json();
    // console.log(data);
    setResults(temp);
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