import React ,{useEffect, useState} from 'react'
import { useDebounce } from 'use-debounce'

import {useStateContext} from '../Contexts/StateContextProvider'
export const Search = () => {

  const [text,setText] =useState("");
  const {setSearchTerm,searchTime,setSearchTerm2,getSugs,Sugs,setSugs}=useStateContext();
  const [debouncedvalue]=useDebounce(text,350);

  // useEffect(()=>{
  //     if(debouncedvalue)setSearchTerm(debouncedvalue);
  // },[debouncedvalue])

const nwfunc = (par) => {
   setSearchTerm(par);

   setText('');
  };


  const nwfunc2 = (par2) => {
    setSearchTerm2(par2);
    getSugs(par2);
    setText(par2);
  };


  return (
    <div className='relative sm:ml-48 md:ml-72 sm:-mt-10 mt-3'>
      <input
      value={text}
      type='text'
      className='sm:w-96 w-80 h-10 dark:bg-gray-200 bg-white border rounded-full shadow-sm outline-none p-6 text-black hover:shadow-lg'
      placeholder='Search on playmaker'
      onChange={(e)=>nwfunc2(e.target.value)}
      />
      {text !== '' && (
        <button type="button" className="absolute top-1.5 right-4 text-2xl text-gray-500 " onClick={() => nwfunc(text)}>
          🔍
        </button>
      )}
      
      <div className='mt-[-5px] ' >
        
      
       { Sugs?.map((suggestion, index) => (
    <button key={index} className="block sm:w-96 w-80 h-10 dark:bg-gray-200 bg-white  p-5px mt-0 pt-0 shadow-sm outline-none pl-6 text-gray-500 text-sm ml-0 mt-0 hover:shadow-lg hover:text-black text-left " onClick={()=>nwfunc2(suggestion)} >{suggestion}</button>
  ))
}
      </div>

      <p className='text-gray-500 text-sm ml-4 mt-2'>Search time : {searchTime.toFixed(2) } ms</p>
      </div>
  )
}
