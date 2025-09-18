'use client';

import { useEffect, useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useRouter } from 'next/navigation';
import { Account, Transfer, accountsAPI, transfersAPI, TransferRequest } from '@/lib/api';
import { toast } from 'react-toastify';
import Navigation from '@/components/Navigation';

export default function TransfersPage() {
  const { isAuthenticated } = useAuth();
  const router = useRouter();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [transfers, setTransfers] = useState<Transfer[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    fromIban: '',
    toIban: '',
    amount: '',
    beneficiaryName: '',
    description: '',
  });

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/auth/login');
      return;
    }

    fetchData();
  }, [isAuthenticated]);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const [accountsData, transfersResponse] = await Promise.all([
        accountsAPI.getUserAccounts(),
        transfersAPI.getUserTransfers(0, 10)
      ]);
      
      setAccounts(accountsData);
      setTransfers(transfersResponse.content);
    } catch (error: any) {
      toast.error('Failed to load data');
      console.error('Error fetching data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const transferData: TransferRequest = {
        fromIban: formData.fromIban,
        toIban: formData.toIban,
        amount: parseFloat(formData.amount),
        beneficiaryName: formData.beneficiaryName,
        description: formData.description,
      };

      await transfersAPI.executeTransfer(transferData);
      toast.success('Transfer executed successfully!');
      
      // Reset form
      setFormData({
        fromIban: '',
        toIban: '',
        amount: '',
        beneficiaryName: '',
        description: '',
      });
      
      // Refresh data
      fetchData();
    } catch (error: any) {
      toast.error(error.response?.data || 'Transfer failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      case 'CANCELLED':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <div className="flex items-center justify-center h-96">
          <div className="text-xl">Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-2xl font-bold text-gray-900">Money Transfers</h1>
            <p className="text-gray-600">Send money to other accounts securely</p>
          </div>
          
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Transfer Form */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  Make a Transfer
                </h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label htmlFor="fromIban" className="block text-sm font-medium text-gray-700">
                      From Account
                    </label>
                    <select
                      id="fromIban"
                      name="fromIban"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      value={formData.fromIban}
                      onChange={handleChange}
                    >
                      <option value="">Select an account</option>
                      {accounts.map((account) => (
                        <option key={account.id} value={account.iban}>
                          {account.accountName} - €{account.balance.toFixed(2)} ({account.iban})
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label htmlFor="toIban" className="block text-sm font-medium text-gray-700">
                      To Account (IBAN)
                    </label>
                    <input
                      type="text"
                      id="toIban"
                      name="toIban"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      placeholder="FR1420041000011234567890186"
                      value={formData.toIban}
                      onChange={handleChange}
                    />
                  </div>

                  <div>
                    <label htmlFor="amount" className="block text-sm font-medium text-gray-700">
                      Amount (€)
                    </label>
                    <input
                      type="number"
                      id="amount"
                      name="amount"
                      min="0.01"
                      step="0.01"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      placeholder="100.00"
                      value={formData.amount}
                      onChange={handleChange}
                    />
                  </div>

                  <div>
                    <label htmlFor="beneficiaryName" className="block text-sm font-medium text-gray-700">
                      Beneficiary Name
                    </label>
                    <input
                      type="text"
                      id="beneficiaryName"
                      name="beneficiaryName"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      placeholder="Jane Smith"
                      value={formData.beneficiaryName}
                      onChange={handleChange}
                    />
                  </div>

                  <div>
                    <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                      Description
                    </label>
                    <input
                      type="text"
                      id="description"
                      name="description"
                      className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      placeholder="Payment description"
                      value={formData.description}
                      onChange={handleChange}
                    />
                  </div>

                  <div className="pt-4">
                    <button
                      type="submit"
                      disabled={isSubmitting}
                      className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                    >
                      {isSubmitting ? 'Processing...' : 'Execute Transfer'}
                    </button>
                  </div>
                </form>

                <div className="mt-4 p-4 bg-yellow-50 rounded-md">
                  <p className="text-sm text-yellow-700">
                    <strong>Transfer Fee:</strong> 0.5% (minimum €0.10, maximum €10.00) will be applied to the transfer amount.
                  </p>
                </div>
              </div>
            </div>

            {/* Recent Transfers */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  Recent Transfers
                </h3>
                <div className="space-y-4">
                  {transfers.length === 0 ? (
                    <p className="text-gray-500 text-center py-4">No transfers found</p>
                  ) : (
                    transfers.map((transfer) => (
                      <div key={transfer.id} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start">
                          <div className="flex-1">
                            <div className="flex items-center justify-between">
                              <p className="text-sm font-medium text-gray-900">
                                €{transfer.amount.toFixed(2)}
                              </p>
                              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(transfer.status)}`}>
                                {transfer.status}
                              </span>
                            </div>
                            <p className="text-sm text-gray-600">
                              To: {transfer.beneficiaryName}
                            </p>
                            <p className="text-sm text-gray-500">
                              {transfer.description}
                            </p>
                            <p className="text-xs text-gray-400">
                              {new Date(transfer.createdAt).toLocaleDateString()}
                            </p>
                          </div>
                        </div>
                        <div className="mt-2 text-xs text-gray-500">
                          Fee: €{transfer.transferFee.toFixed(2)} | Ref: {transfer.referenceNumber}
                        </div>
                      </div>
                    ))
                  )}
                </div>
                {transfers.length > 0 && (
                  <div className="mt-4">
                    <button
                      onClick={() => router.push('/transactions')}
                      className="text-indigo-600 hover:text-indigo-500 text-sm"
                    >
                      View all transactions →
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}