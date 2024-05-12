import React, { useEffect , useState } from 'react';
import { useLocation } from 'react-router-dom';
import ReactPlayer from 'react-player';

import { useStateContext } from '../Contexts/StateContextProvider';
import { Loading } from './Loading';

export const Results = () => {
  const { results, loading, getResults, searchTerm } = useStateContext();

  const location =useLocation();
  const [currentPage, setCurrentPage] = useState(1);

  const resultsPerPage = 10;


  const indexOfLastResult = currentPage * resultsPerPage;
  const indexOfFirstResult = indexOfLastResult - resultsPerPage;
  const currentResults = results.slice(indexOfFirstResult, indexOfLastResult);

  useEffect(() => {
    
      if(searchTerm !=='')
        getResults(`?query=${searchTerm}`);
     
    
  
  }, [searchTerm]);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
 
};

  if (loading) return <Loading />;


console.log(results.results);
      return (
        <div>
        <div className="sm:px-56 flex flex-wrap justify-between space-y-6 mt-10">
          {currentResults?.map(({ url, title ,description}, index) => (
            <div key={index} className="md:w-2/5 w-full">
              <a href={url} target="_blank" rel="noreferrer">
                <p className="text-sm">{url.length > 30 ? url.substring(0, 30) : url}</p>
                <p className="text-lg hover:underline dark:text-blue-300 text-blue-700  ">{title}</p>
                <p className='text-md'>
              
              {/* {description.length > 200 ? description=description.substring(0, 200) : description = description}  */}
        
              {description?.split(' ').map((word, index) => (

        <span key={index} className={searchTerm.toLowerCase().includes(word.toLowerCase()) ? 'font-bold' : ''}>
          {word + " "}
            </span>
      ))}
              </p>
            
              </a>
            </div>
          ))}
           </div>

<div className="mt-10 flex items-center justify-center ">
                {Array.from({ length: Math.ceil(results?.length / resultsPerPage) }, (_, i) => i + 1).map((pageNumber) => (
                    <button
                        key={pageNumber}
                        className={currentPage === pageNumber ? 'font-bold ml-4 bg-green-500 w-8 h-8 rounded-full hover:shadow-lg hover:text-white dark:hover:text-black' : ' bg-gray-200 dark:bg-gray-800 ml-4  w-8 h-8 rounded-full hover:shadow-lg hover:text-white dark:hover:text-black'}
                        onClick={() => handlePageChange(pageNumber)}
                    >
                        {pageNumber}
                    </button>
                ))}
                </div>
       
        </div>
      );

    
};