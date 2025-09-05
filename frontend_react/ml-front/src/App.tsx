import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function App() {
  const [count, setCount] = useState(0);
  const [data, setData] = useState<string>("");

  async function getRequest(){
    fetch("http://localhost:8080/api/v1/test").then(async (response) =>{
      setData(await response.text());
    })
  }
  return (
    <div className='font-mono bg-bg-alt p-4 rounded-lg m-2 justify-around' >
      <button 
        className='btn ' 
        onClick={getRequest}>
          <p>Response is: <span className=''>{data}</span></p>
      </button>
      <button 
        className='btn-sec' 
        onClick={getRequest}>
          <p>Response is: <span className=''>{data}</span></p>
      </button>
      <div className='bg-acc p-0.5 rounded-lg m-2'></div>
    </div>
  )
}

export default App
