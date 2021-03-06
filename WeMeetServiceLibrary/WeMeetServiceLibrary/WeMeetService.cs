﻿using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WeMeetServiceLibrary
{
    public class WeMeetService : IWeMeetService
    {
        private ConnectionManager connectionManager;

        #region R1
        public bool RegisterPhoneNumber(string phoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spRegisterPhoneNumber", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public bool IsRegisteredPhoneNumber(string phoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spIsRegistered", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public bool UnRegisterPhoneNumber(string phoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spUnRegisterPhoneNumber", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public bool UpdateLocation(string phoneNumber, string latitude, string longitude)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spUpdateLocation", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));
            cmd.Parameters.Add(new SqlParameter("@Latitude", latitude));
            cmd.Parameters.Add(new SqlParameter("@Longitude", longitude));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public Location GetLocation(string requesterPhoneNumber, string phoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spGetLocation", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            SqlParameter latitude = cmd.Parameters.Add(new SqlParameter("@Latitude", SqlDbType.VarChar, 20));
            latitude.Direction = ParameterDirection.Output;

            SqlParameter longitude = cmd.Parameters.Add(new SqlParameter("@Longitude", SqlDbType.VarChar, 20));
            longitude.Direction = ParameterDirection.Output;

            SqlParameter date = cmd.Parameters.Add(new SqlParameter("@Date", SqlDbType.DateTime));
            date.Direction = ParameterDirection.Output;

            cmd.ExecuteNonQuery();

            connectionManager.Close();

            return new Location
            {
                Latitude = latitude.Value.ToString(),
                Longitude = longitude.Value.ToString(),
                Date = date.Value.ToString()
            };
        }

        public bool IsRequestSentTo(string fromPhoneNumber, string toPhoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spIsRequestSentTo", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@FromPhoneNumber", fromPhoneNumber));
            cmd.Parameters.Add(new SqlParameter("@ToPhoneNumber", toPhoneNumber));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public List<string> GetLocationRequests(string phoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spGetLocationRequests", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            SqlDataReader sdr = cmd.ExecuteReader();

            List<string> result = new List<string>();

            if (sdr.HasRows)
            {
                while (sdr.Read())
                {
                    result.Add(sdr[0].ToString());
                }
            }

            sdr.Close();
            connectionManager.Close();

            return result;
        }

        public bool SendLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spSendLocationSharingRequest", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@FromPhoneNumber", fromPhoneNumber));
            cmd.Parameters.Add(new SqlParameter("@ToPhoneNumber", toPhoneNumber));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public bool AcceptLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();
            SqlCommand cmd;
            SqlDataReader sdr;
            string fromList = null, toList = null;

            cmd = new SqlCommand("spGetSharedLocationList", connectionManager.con);
            cmd.CommandType = CommandType.StoredProcedure;

            //fetching shared location list doe both of the numbers
            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", fromPhoneNumber));
            sdr = cmd.ExecuteReader();
            if (sdr.HasRows)
            {
                sdr.Read();
                fromList = sdr[0].ToString();
                sdr.Close();
            }

            cmd = new SqlCommand("spGetSharedLocationList", connectionManager.con);
            cmd.CommandType = CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", fromPhoneNumber));
            sdr = cmd.ExecuteReader();
            if (sdr.HasRows)
            {
                sdr.Read();
                toList = sdr[0].ToString();
                sdr.Close();
            }

            //addinf phone numbers to shared list
            if (!fromList.Contains(toPhoneNumber))
            {
                cmd = new SqlCommand("spAddToSharedLocationList", connectionManager.con);
                cmd.CommandType = CommandType.StoredProcedure;

                cmd.Parameters.Add(new SqlParameter("@RequesterPhoneNumber", fromPhoneNumber));
                cmd.Parameters.Add(new SqlParameter("@SharedPhoneNumber", toPhoneNumber));

                cmd.ExecuteNonQuery();
            }
            if (!toList.Contains(fromPhoneNumber))
            {
                cmd = new SqlCommand("spAddToSharedLocationList", connectionManager.con);
                cmd.CommandType = CommandType.StoredProcedure;

                cmd.Parameters.Add(new SqlParameter("@RequesterPhoneNumber", toPhoneNumber));
                cmd.Parameters.Add(new SqlParameter("@SharedPhoneNumber", fromPhoneNumber));

                cmd.ExecuteNonQuery();
            }

            //removing request
            bool result = DeleteLocationSharingRequest(fromPhoneNumber, toPhoneNumber);

            connectionManager.Close();

            return result;
        }

        public bool DeleteLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spDeleteLocationSharingRequest", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@FromPhoneNumber", fromPhoneNumber));
            cmd.Parameters.Add(new SqlParameter("@ToPhoneNumber", toPhoneNumber));

            SqlParameter retval = cmd.Parameters.Add("@ret_val", System.Data.SqlDbType.Int);
            retval.Direction = ParameterDirection.ReturnValue;

            cmd.ExecuteNonQuery();

            int result = (int)cmd.Parameters["@ret_val"].Value;

            connectionManager.Close();

            return result > 0;
        }

        public List<NearBy> GetFriendsNearBy(string phoneNumber)
        {
            List<NearBy> friendsNearBy = new List<NearBy>();

            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spGetSharedLocationList", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            //fetching shared location list
            string[] sharedLocationList = cmd.ExecuteScalar().ToString().Split(',');

            //closing connection
            connectionManager.Close();

            //get location of the requestore
            Location rLocation = GetLocation(phoneNumber, phoneNumber);
            if (string.IsNullOrEmpty(rLocation.Latitude) || string.IsNullOrEmpty(rLocation.Latitude))
                return friendsNearBy;

            DistanceCalculator dCalculator = new DistanceCalculator();

            //calulating distance for each contact in shared list
            for (int i = 0; i < sharedLocationList.Length; i++)
            {
                //getting location of contact
                Location location = GetLocation(phoneNumber, sharedLocationList[i]);

                if (string.IsNullOrEmpty(location.Latitude) || string.IsNullOrEmpty(location.Latitude))
                    continue;

                //calculating distance between requester and contact
                double lat1 = Convert.ToDouble(rLocation.Latitude);
                double long1 = Convert.ToDouble(rLocation.Longitude);
                double lat2 = Convert.ToDouble(location.Latitude);
                double long2 = Convert.ToDouble(location.Longitude);
                double distance = dCalculator.distance(lat1, long1, lat2, long2, 'K');

                //adding to near by list if distance is less than or equal to 5 KM
                if (distance <= 5)
                {
                    friendsNearBy.Add(new NearBy
                        {
                            Location = location,
                            PhoneNumber = sharedLocationList[i],
                            Distance = distance
                        });
                }
            }

            return friendsNearBy;
        }

        public string GetSharedLocationList(string phoneNumber)
        {
            connectionManager = new ConnectionManager(ConfigurationManager.ConnectionStrings["WeMeetDb"].ConnectionString);
            connectionManager.Open();

            SqlCommand cmd = new SqlCommand("spGetSharedLocationList", connectionManager.con);
            cmd.CommandType = System.Data.CommandType.StoredProcedure;

            cmd.Parameters.Add(new SqlParameter("@PhoneNumber", phoneNumber));

            //fetching shared location list
            string result = cmd.ExecuteScalar().ToString();

            //closing connection
            connectionManager.Close();

            return result;
        }
        #endregion

        #region R2

        public void CreateGroup(string phoneNumber, string groupName)
        {
            throw new NotImplementedException();
        }

        public void RenameGroup(string phoneNumber, string groupName)
        {
            throw new NotImplementedException();
        }

        public void AddGroupMember(string requesterPhoneNumber, string groupName, string phoneNumber)
        {
            throw new NotImplementedException();
        }

        public void DeleteGroupMember(string requesterPhoneNumber, string groupName, string phoneNumber)
        {
            throw new NotImplementedException();
        }

        public void DeleteGroup(string phoneNumber, string groupName)
        {
            throw new NotImplementedException();
        }

        public List<string> GetGroups(string phoneNumber)
        {
            throw new NotImplementedException();
        }

        public List<string> GetGroupMembers(string phoneNumber, string groupName)
        {
            throw new NotImplementedException();
        }
        #endregion
    }
}
