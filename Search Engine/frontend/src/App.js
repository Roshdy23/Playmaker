
import React , {useState} from "react";

import { Navbar } from "./components/Navbar";
import { Footer } from "./components/Footer";
import {RouterComponent } from "./components/Routers";

const App =()=> {

  const [darkTheme,setDarkTheme] = useState(false);
  const toggleMode = () => {
    setDarkTheme(!darkTheme);
  };
  return (
    <div className={darkTheme ? "dark" : ""}>
      <div className="bg-gray-100 dark:bg-gray-900 dark:text-gray-200 min-h-screen">
      
        <Navbar darkTheme={darkTheme} setDarkTheme={setDarkTheme}/>
        <RouterComponent/>
        <Footer/>
        

      </div>
    </div>

  );
}

export default App;
