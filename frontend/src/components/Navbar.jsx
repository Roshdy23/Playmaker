import React from 'react'
import {Link} from "react-router-dom"
import { Search } from './Search'

export const Navbar = ({darkTheme,setDarkTheme}) => {
  
  return (
    <div className='p-5 pb-7 flex flex-wrap sm:justify-between justify-center items-center border-b dark:border-gray-700 border-gray-200 '>
        <div className='flex justify-between items-center space-x-5 w-screen'>
            <Link to="/">  
            <p className='text-2xl bg-green-500 font-bold px-2 text-white py-1 rounded dark:bg-gray-500 dark:text-gray-900'> Playmaker ⚽ </p>
          </Link>
          <button type="button" onClick={()=>setDarkTheme(!darkTheme)}  className='text-2xl hover:shadow-lg'>
          {darkTheme ? '🌕':'🌑'}
            </button>

          
        </div>
        <Search ></Search>

        </div>
  )
}
