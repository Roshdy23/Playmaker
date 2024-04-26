import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import ReactPlayer from 'react-player';

import { useStateContext } from '../Contexts/StateContextProvider';
import { Loading } from './Loading';

export const Results = () => {
  const { results, loading, getResults, searchTerm } = useStateContext();

  const location =useLocation();

  useEffect(() => {
    
      if(searchTerm !=='')
        getResults(`?query=${searchTerm}=40&related_keywords=true`);
      
  
  }, [searchTerm]);

  

  if (loading) return <Loading />;


console.log(results.results);
      return (
        <div className="sm:px-56 flex flex-wrap justify-between space-y-6 mt-10">
          {results?.results?.map(({ url, title ,description}, index) => (
            <div key={index} className="md:w-2/5 w-full">
              <a href={url} target="_blank" rel="noreferrer">
                <p className="text-sm">{url.length > 30 ? url.substring(0, 30) : url}</p>
                <p className="text-lg hover:underline dark:text-blue-300 text-blue-700  ">{title}</p>
                <p className='text-md'>{description.length > 200 ? description.substring(0, 200) : description}</p>
              </a>
            </div>
          ))}
        </div>
      );

    
};