export function Header(){
    return (
        <div className=' px-4 m-2 grid-cols-2 inline-grid justify-center-safe'>
            <div className="header w-max">
                <h1 className=''>Logo TMP</h1>
                {/* <img src={logo} className="logo w-screen h-auto" alt="logo" /> */}
            </div>
            <div className="nav w-max flex justify-center">
                <h1 className=''>Navbar</h1>
                
            </div>
        </div>
    );
}