import { useState } from 'react'
import './App.css'
import { api } from './services/api';
import { Header } from './common/header';

function App() {
  const [data, setData] = useState<string>("");

  async function getRequest(){
    setData(await api.test());
  }
  return (
    <>
      <Header></Header>
      <div className='card' >
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
        <div className='card' >
          <div className='bg-acc p-0.5 rounded-lg m-2'></div>

        </div>
      </div>
    </>
  )
}

export default App
