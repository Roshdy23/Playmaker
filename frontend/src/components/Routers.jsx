import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Results } from "./Results";

export const RouterComponent = () => {
  return (
    <div className='p-4'>
      <Routes>
        <Route path='/' element={<Navigate to='/search' />} />
        <Route path='/search' element={<Results />} />
    
      </Routes>
    </div>
  );
};
