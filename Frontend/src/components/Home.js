import React, {useEffect} from 'react';
import axios from "axios";
import {GET_Profils,LOCAL_HOST_Profil,UPDATE_Profil} from "../constants/back";


export default  function Home()
{
    const [user, setUser] = React.useState([]);
    useEffect(() => {
        loading();
    },[]);
    const  loading=async ()=>{

        const  result=await axios.get("http://localhost:8080/profil");
        setUser(result.data);
    }



    return (<table className="table">
            <thead>
            <tr>
                <th scope="col">First</th>
                <th scope="col">Last</th>
                <th scope="col">Handle</th>
            </tr>
            </thead>

            <tbody>
            {
                user.map((user, index) =>
                    <tr>
                        <td>{user.bio}</td>
                        <td>{user.centresInteret}</td>
                    </tr>
                )
            }

            </tbody>
        </table>


    )
}